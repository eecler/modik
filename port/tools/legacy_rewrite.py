#!/usr/bin/env python3
"""Batch 1.20.1 -> 1.21.1 (NeoForge) rewriter for port/wip.

Two kinds of output:
  * mechanical rewrites applied in place (only with --apply; default is a dry run), and
  * flags.tsv - call sites the script refuses to touch (packets, LazyOptional plumbing,
    codec-based recipes...) so the manual work is a targeted list, not a search.

compat/ and mixin/ are excluded entirely: they are not in the source set, mixins are gated by
destroy.mixins.json, and compat only classloads behind Mods.X.executeIfInstalled.

Usage:
  python3 port/tools/legacy_rewrite.py            # dry run: per-category counts
  python3 port/tools/legacy_rewrite.py --apply    # write changes + port/tools/flags.tsv
"""

import re
import sys
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
WIP = ROOT / "port" / "wip"
EXCLUDE_DIRS = {"compat", "mixin"}

LEGACY_PKG = "petrolpark.mc.destroy.legacy"

# --- 1. Leftover Forge -> NeoForge import re-paths (prefix map, longest first) ---------------
IMPORT_MAP = [
    ("net.minecraftforge.api.distmarker", "net.neoforged.api.distmarker"),
    ("net.minecraftforge.eventbus.api",   "net.neoforged.bus.api"),
    ("net.minecraftforge.fml",            "net.neoforged.fml"),
    ("net.minecraftforge.registries",     "net.neoforged.neoforge.registries"),
    ("net.minecraftforge.network",        "net.neoforged.neoforge.network"),
    ("net.minecraftforge.items",          "net.neoforged.neoforge.items"),
    ("net.minecraftforge.fluids",         "net.neoforged.neoforge.fluids"),
    ("net.minecraftforge.energy",         "net.neoforged.neoforge.energy"),
    ("net.minecraftforge.client",         "net.neoforged.neoforge.client"),
    ("net.minecraftforge.event",          "net.neoforged.neoforge.event"),
    ("net.minecraftforge.common",         "net.neoforged.neoforge.common"),
]

# --- 2. Simple regex rewrites ----------------------------------------------------------------
# Conservative receiver: ident chains with optional no-arg calls (stack, this.stack, e.getItem()).
RECV = r"(?:this\.)?[A-Za-z_]\w*(?:\.[A-Za-z_]\w*\(\))*"

REWRITES = [
    ("stack.getOrCreateTag",
     re.compile(rf"(?<![\w.])({RECV})\.getOrCreateTag\(\)"),
     r"LegacyNBT.getOrCreateTag(\1)", "LegacyNBT"),
    ("stack.getTag",
     re.compile(rf"(?<![\w.])({RECV})\.getTag\(\)"),
     r"LegacyNBT.getTag(\1)", "LegacyNBT"),
    ("stack.hasTag",
     re.compile(rf"(?<![\w.])({RECV})\.hasTag\(\)"),
     r"LegacyNBT.hasTag(\1)", "LegacyNBT"),
    ("recipe lookup",
     re.compile(rf"(?<![\w.])(?:{RECV})\.getRecipeManager\(\)\.(getRecipeFor|getAllRecipesFor)\("),
     r"LegacyRecipes.\1(", "LegacyRecipes"),
    ("BE read/write signature",
     re.compile(r"void (read|write)\(CompoundTag (\w+), boolean (\w+)\)"),
     r"void \1(CompoundTag \2, HolderLookup.Provider registries, boolean \3)",
     "net.minecraft.core.HolderLookup"),
    ("BE read/write super call",
     re.compile(r"super\.(read|write)\((\w+), (\w+)\);"),
     r"super.\1(\2, registries, \3);", None),
]

# setTag has an argument -> needs balanced-paren handling, done separately.
# (?!LegacyNBT) keeps re-runs from matching the shim call itself.
SETTAG = re.compile(rf"(?<![\w.])(?!LegacyNBT\b)({RECV})\.setTag\(")
NEW_RL = re.compile(r"new ResourceLocation\(")

# --- 3. Flag-only categories (real rewrites, listed for manual work) --------------------------
FLAGS = [
    ("LazyOptional",        re.compile(r"\bLazyOptional\b")),
    ("ForgeCapabilities",   re.compile(r"\bForgeCapabilities\b")),
    ("getCapability call",  re.compile(r"\.getCapability\(")),
    ("packet (FriendlyByteBuf)", re.compile(r"\bFriendlyByteBuf\b")),
    ("recipe codec rewrite", re.compile(r"ProcessingRecipeBuilder|fromNetwork\(|toNetwork\(")),
    ("block use() override", re.compile(r"InteractionResult use\(")),
    ("serializeNBT",        re.compile(r"serializeNBT\(\)|deserializeNBT\(")),
    ("RegistryObject",      re.compile(r"\bRegistryObject\b")),
    ("LegacyNBT copy semantics", re.compile(r"LegacyNBT\.getOrCreateTag")),  # review: returned tag is a copy
]


def find_close(s: str, start: int) -> int:
    """Index of the ')' matching the '(' at start-1 (start is just after the open paren)."""
    depth, i = 1, start
    while i < len(s):
        c = s[i]
        if c == '(':
            depth += 1
        elif c == ')':
            depth -= 1
            if depth == 0:
                return i
        elif c in '"\'':
            q = c
            i += 1
            while i < len(s) and s[i] != q:
                i += 2 if s[i] == '\\' else 1
        i += 1
    return -1


def top_level_commas(s: str) -> int:
    depth = count = i = 0
    while i < len(s):
        c = s[i]
        if c == '(':
            depth += 1
        elif c == ')':
            depth -= 1
        elif c == ',' and depth == 0:
            count += 1
        elif c in '"\'':
            q = c
            i += 1
            while i < len(s) and s[i] != q:
                i += 2 if s[i] == '\\' else 1
        i += 1
    return count


def rewrite_resource_location(text: str, stats: Counter) -> str:
    out, pos = [], 0
    for m in NEW_RL.finditer(text):
        close = find_close(text, m.end())
        if close < 0:
            continue
        args = text[m.end():close]
        repl = ("ResourceLocation.fromNamespaceAndPath(" if top_level_commas(args) >= 1
                else "ResourceLocation.parse(")
        out.append(text[pos:m.start()])
        out.append(repl)
        out.append(args)
        pos = close  # keep the ')'
        stats["new ResourceLocation"] += 1
    out.append(text[pos:])
    return "".join(out)


def rewrite_settag(text: str, stats: Counter) -> tuple[str, bool]:
    used = False
    pos = 0
    while True:
        m = SETTAG.search(text, pos)
        if not m:
            return text, used
        close = find_close(text, m.end())
        if close < 0:
            return text, used
        recv, args = m.group(1), text[m.end():close]
        repl = f"LegacyNBT.setTag({recv}, {args})"
        text = text[:m.start()] + repl + text[close + 1:]
        pos = m.start() + len(repl)  # continue after the replacement so it cannot re-match
        stats["stack.setTag"] += 1
        used = True


def add_import(text: str, fqcn: str) -> str:
    simple = fqcn.rsplit(".", 1)[1]
    if re.search(rf"^import (static )?{re.escape(fqcn)};", text, re.M):
        return text
    return re.sub(r"^(package [\w.]+;\n)", rf"\1\nimport {fqcn};", text, count=1, flags=re.M)


def process(path: Path, apply: bool, stats: Counter, flags: list) -> None:
    text = orig = path.read_text()
    needed_imports = set()

    for old, new in IMPORT_MAP:
        n = text.count(f"import {old}")
        if n:
            text = text.replace(f"import {old}", f"import {new}")
            stats["forge import re-path"] += n

    # 1.20.1 config root -> the skeleton's DestroyConfigs accessors; effect holders lose .get().
    for old, new in [
        ("DestroyAllConfigs.SERVER.", "DestroyConfigs.server()."),
        ("DestroyAllConfigs.CLIENT.", "DestroyConfigs.client()."),
        ("DestroyAllConfigs.COMMON.", "DestroyConfigs.common()."),
        ("import petrolpark.mc.destroy.config.DestroyAllConfigs;", "import petrolpark.mc.destroy.config.DestroyConfigs;"),
    ]:
        n = text.count(old)
        if n:
            text = text.replace(old, new)
            stats["config remap"] += n
    text, n = re.subn(r"DestroyMobEffects\.([A-Z_]+)\.get\(\)", r"DestroyMobEffects.\1", text)
    if n:
        stats["mob effect holder"] += n

    # Provider threading: 1.21.1 made every serialization entry point registry-aware. Where the
    # Provider is genuinely just "the current level's registries", LegacyRegistries supplies it.
    text, n = re.subn(rf"(?<![\w.])({RECV})\.serializeNBT\(\)", r"LegacyRegistries.serializeNBT(\1)", text)
    if n:
        stats["serializeNBT provider"] += n
        needed_imports.add(f"{LEGACY_PKG}.LegacyRegistries")
    text, n = re.subn(rf"(?<![\w.])({RECV})\.deserializeNBT\((?!\s*(?:access|registries|provider|lookup)\b)([^;]+?)\);",
                      r"LegacyRegistries.deserializeNBT(\1, \2);", text)
    if n:
        stats["deserializeNBT provider"] += n
        needed_imports.add(f"{LEGACY_PKG}.LegacyRegistries")

    # Straight renames verified against the 1.21.1 jars.
    for old, new, label in [
        ("saturationMod(", "saturationModifier(", "FoodProperties builder rename"),
        ("PollutionHelper.pollutionEnabled()", "PollutionHelper.isPollutionEnabled()", "pollution helper rename"),
    ]:
        n = text.count(old)
        if n:
            text = text.replace(old, new)
            stats[label] += n

    # Pollution went from an enum to a registry: Pollution.PollutionType.SMOG is now the
    # DestroyPollutionTypes entry. values()/.max have no mechanical equivalent and stay flagged.
    text, n = re.subn(r"(?<![\w.])(?:Pollution\.)?PollutionType\.(SMOG|GREENHOUSE|OZONE_DEPLETION|ACID_RAIN)\b(?!\s*\()",
                      r"DestroyPollutionTypes.\1.get()", text)
    if n:
        stats["pollution type entry"] += n
        needed_imports.add("petrolpark.mc.destroy.DestroyPollutionTypes")
    text = text.replace("import petrolpark.mc.destroy.core.pollution.Pollution.PollutionType;",
                        "import petrolpark.mc.destroy.core.pollution.PollutionType;")

    # The capability classes have no NeoForge counterpart at the same path - re-pathing them only
    # disguises a legacy import that needs a real rewrite. Put those back; they stay flagged.
    for cap in ("common.capabilities", "common.util.LazyOptional"):
        broken = f"import net.neoforged.neoforge.{cap}"
        if broken in text:
            stats["forge import re-path"] -= text.count(broken)
            text = text.replace(broken, f"import net.minecraftforge.{cap}")

    for name, rx, repl, imp in REWRITES:
        text, n = rx.subn(repl, text)
        if n:
            stats[name] += n
            if imp:
                needed_imports.add(imp if "." in imp else f"{LEGACY_PKG}.{imp}")

    text2, used = rewrite_settag(text, stats)
    if used:
        needed_imports.add(f"{LEGACY_PKG}.LegacyNBT")
    text = rewrite_resource_location(text2, stats)

    for imp in sorted(needed_imports):
        text = add_import(text, imp)

    rel = path.relative_to(ROOT)
    for name, rx in FLAGS:
        for i, line in enumerate(text.splitlines(), 1):
            if rx.search(line):
                flags.append((name, str(rel), i, line.strip()[:120]))

    if apply and text != orig:
        path.write_text(text)
        stats["files changed"] += 1


def main() -> None:
    apply = "--apply" in sys.argv
    stats: Counter = Counter()
    flags: list = []

    for path in sorted(WIP.rglob("*.java")):
        parts = path.relative_to(WIP).parts
        if parts[0] in EXCLUDE_DIRS or (len(parts) > 3 and parts[3] in EXCLUDE_DIRS):
            continue
        process(path, apply, stats, flags)

    print(f"{'APPLIED' if apply else 'DRY RUN'} on {WIP}")
    for name, n in stats.most_common():
        print(f"{n:6d}  {name}")

    if apply:
        report = ROOT / "port" / "tools" / "flags.tsv"
        with report.open("w") as f:
            f.write("category\tfile\tline\tcode\n")
            for row in sorted(flags):
                f.write("\t".join(map(str, row)) + "\n")
        print(f"\nflag report: {report} ({len(flags)} rows)")
    else:
        flag_counts = Counter(name for name, *_ in flags)
        print("\nflag-only (manual work remaining):")
        for name, n in flag_counts.most_common():
            print(f"{n:6d}  {name}")


if __name__ == "__main__":
    main()
