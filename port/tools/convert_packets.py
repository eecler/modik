#!/usr/bin/env python3
"""Convert 1.20.1 S2CPacket/C2SPacket classes to the 1.21.1 Catnip payload pattern.

The legacy shape is uniform - fields written in toBytes(FriendlyByteBuf), read back in a
(FriendlyByteBuf) constructor, behaviour in handle(Supplier<Context>). The 1.21.1 shape is a
record implementing Clientbound/ServerboundPacketPayload with a StreamCodec.

Field order comes from toBytes; the buffer method names give the codec. Anything using a buffer
method not in CODECS, or more fields than StreamCodec.composite supports, is skipped and listed -
those get written by hand rather than guessed at.

Usage: python3 port/tools/convert_packets.py [--apply]
"""

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
WIP = ROOT / "port/wip"
OUT = ROOT / "src/main/java"

# buffer method -> (java type, StreamCodec expression, extra imports)
CODECS = {
    "writeBoolean":  ("boolean", "ByteBufCodecs.BOOL", []),
    "writeVarInt":   ("int", "ByteBufCodecs.VAR_INT", []),
    "writeInt":      ("int", "ByteBufCodecs.INT", []),
    "writeByte":     ("int", "ByteBufCodecs.BYTE", []),
    "writeShort":    ("int", "ByteBufCodecs.SHORT", []),
    "writeFloat":    ("float", "ByteBufCodecs.FLOAT", []),
    "writeDouble":   ("double", "ByteBufCodecs.DOUBLE", []),
    "writeUtf":      ("String", "ByteBufCodecs.STRING_UTF8", []),
    "writeBlockPos": ("BlockPos", "BlockPos.STREAM_CODEC", ["net.minecraft.core.BlockPos"]),
    "writeUUID":     ("UUID", "UUIDUtil.STREAM_CODEC", ["java.util.UUID", "net.minecraft.core.UUIDUtil"]),
    "writeItem":     ("ItemStack", "ItemStack.STREAM_CODEC", ["net.minecraft.world.item.ItemStack"]),
    "writeNbt":      ("CompoundTag", "ByteBufCodecs.COMPOUND_TAG", ["net.minecraft.nbt.CompoundTag"]),
    "writeResourceLocation": ("ResourceLocation", "ResourceLocation.STREAM_CODEC",
                              ["net.minecraft.resources.ResourceLocation"]),
    "writeFluidStack": ("FluidStack", "FluidStack.STREAM_CODEC",
                        ["net.neoforged.neoforge.fluids.FluidStack"]),
}

CLASS_RX = re.compile(r"public class (\w+) extends (S2CPacket|C2SPacket)")
WRITE_RX = re.compile(r"buffer\.(write\w+)\(([^;]+?)\);")
PKG_RX = re.compile(r"^package ([\w.]+);", re.M)


def snake(name):
    name = re.sub(r"(S2C|C2S)Packet$", "", name)
    return re.sub(r"(?<!^)(?=[A-Z])", "_", name).lower()


def body_of(text, method_sig):
    m = re.search(method_sig, text)
    if not m:
        return None
    i = text.index("{", m.end() - 1)
    depth, j = 1, i + 1
    while j < len(text) and depth:
        if text[j] == "{":
            depth += 1
        elif text[j] == "}":
            depth -= 1
        j += 1
    return text[i + 1:j - 1].strip()


# Already shipped by Petrolpark Library 1.5.0 or by the upstream skeleton - porting these would
# duplicate a class that exists, so the call sites should use the existing one instead.
SUPERSEDED = {
    "ExtraInventorySizeChangeS2CPacket": "library ExtraInventorySizeChangePacket",
    "RequestInventoryFullStateC2SPacket": "library RequestInventoryFullStatePacket",
    "RedstoneProgramSyncReplyS2CPacket": "library RefreshRedstoneProgrammerScreenPacket",
    "RedstoneProgrammerPowerChangedS2CPacket": "library ChangeRedstoneProgrammerPowerPacket",
    "RedstoneProgramSyncC2SPacket": "library SetRedstoneProgramPacket",
    "LevelPollutionS2CPacket": "skeleton LevelPollutionPacket",
    "SyncChunkPollutionS2CPacket": "skeleton ChunkPollutionPacket",
}


def convert(path):
    if path.stem in SUPERSEDED:
        return None, f"superseded by {SUPERSEDED[path.stem]}"
    text = path.read_text()
    cm = CLASS_RX.search(text)
    if not cm:
        return None, "not a legacy packet"
    name, kind = cm.group(1), cm.group(2)
    pkg = PKG_RX.search(text).group(1)

    to_bytes = body_of(text, r"public void toBytes\(FriendlyByteBuf buffer\)")
    if to_bytes is None:
        return None, "no toBytes"
    writes = WRITE_RX.findall(to_bytes)
    stripped = WRITE_RX.sub("", to_bytes).strip()
    if stripped:
        return None, "toBytes has logic beyond plain writes"

    fields, extra_imports = [], set()
    for meth, arg in writes:
        if meth not in CODECS:
            return None, f"unsupported buffer method {meth}"
        jtype, codec, imps = CODECS[meth]
        fname = re.sub(r"^this\.", "", arg.strip()).split(".")[0]
        if not re.fullmatch(r"\w+", fname):
            fname = f"value{len(fields)}"
        fields.append((jtype, fname, codec))
        extra_imports.update(imps)
    if len(fields) > 6:
        return None, f"{len(fields)} fields exceeds StreamCodec.composite"

    handle = body_of(text, r"public boolean handle\(Supplier<Context> \w+\)")
    if handle is None:
        return None, "no handle"
    # Unwrap the enqueueWork indirection; the Catnip handler already runs on the main thread.
    handle = re.sub(r"\w+\.get\(\)\.enqueueWork\(\(\)\s*->\s*\{", "{", handle)
    handle = re.sub(r"(?:\w+)\.enqueueWork\(\(\)\s*->\s*\{", "{", handle)
    handle = re.sub(r"NetworkEvent\.Context \w+ = \w+\.get\(\);", "", handle)
    handle = re.sub(r"\w+\.get\(\)\.getSender\(\)", "player", handle)
    handle = re.sub(r"\w+\.getSender\(\)", "player", handle)
    handle = re.sub(r"return true;\s*$", "", handle).strip()
    handle = re.sub(r"\}\s*\);\s*$", "}", handle).strip()

    clientbound = kind == "S2CPacket"
    iface = "ClientboundPacketPayload" if clientbound else "ServerboundPacketPayload"
    param = "LocalPlayer player" if clientbound else "ServerPlayer player"
    param_imp = ("net.minecraft.client.player.LocalPlayer" if clientbound
                 else "net.minecraft.server.level.ServerPlayer")

    comps = ", ".join(f"{t} {n}" for t, n, _ in fields)
    if fields:
        parts = ",\n        ".join(f"{c}, {name}::{n}" for t, n, c in fields)
        codec = (f"    public static final StreamCodec<RegistryFriendlyByteBuf, {name}> STREAM_CODEC = "
                 f"StreamCodec.composite(\n        {parts},\n        {name}::new\n    );")
    else:
        codec = (f"    public static final StreamCodec<RegistryFriendlyByteBuf, {name}> STREAM_CODEC = "
                 f"StreamCodec.unit(new {name}());")

    imports = sorted({
        "net.minecraft.network.RegistryFriendlyByteBuf",
        "net.minecraft.network.codec.ByteBufCodecs",
        "net.minecraft.network.codec.StreamCodec",
        "net.createmod.catnip.net.base." + iface,
        param_imp,
        "petrolpark.mc.destroy.DestroyPackets",
    } | extra_imports)
    # keep whatever the original imported from the mod itself - the handler body still needs it
    for imp in re.findall(r"^import ([\w.]+);", text, re.M):
        if imp.startswith("petrolpark.") and "library.network" not in imp:
            imports.append(imp)

    src = f"""package {pkg};

{chr(10).join(f"import {i};" for i in sorted(set(imports)))}

/**
 * PORT (1.21.1): mechanically converted from the 1.20.1 {kind} by port/tools/convert_packets.py.
 */
public record {name}({comps}) implements {iface} {{

{codec}

    @Override
    public PacketTypeProvider getTypeProvider() {{
        return DestroyPackets.{snake(name).upper()};
    }};

    @Override
    public void handle({param}) {{
        {handle}
    }};

}};
"""
    return (pkg, name, kind, src), None


def main():
    apply = "--apply" in sys.argv
    done, skipped = [], []
    for path in sorted(WIP.rglob("*S2CPacket.java")) + sorted(WIP.rglob("*C2SPacket.java")):
        res, why = convert(path)
        if res is None:
            skipped.append((path.name, why))
            continue
        pkg, name, kind, src = res
        out = OUT / pkg.replace(".", "/") / f"{name}.java"
        done.append((name, kind, out))
        if apply:
            out.parent.mkdir(parents=True, exist_ok=True)
            out.write_text(src)

    print(f"converted: {len(done)}   skipped: {len(skipped)}")
    for n, k, _ in done:
        print(f"  + {n} ({k})")
    for n, why in skipped:
        print(f"  - {n}: {why}")
    if apply and done:
        print("\nadd to DestroyPackets:")
        for n, k, _ in sorted(done):
            print(f"    {snake(n).upper()}({n}.class, {n}.STREAM_CODEC),")


if __name__ == "__main__":
    main()
