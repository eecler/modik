package petrolpark.mc.destroy.legacy;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.minecraft.nbt.CompoundTag;

/**
 * Ambient registry access, for the 1.20.1 serialization signatures that took no
 * {@link HolderLookup.Provider}.
 * <p>
 * 1.21.1 threads a Provider through every serialization entry point, because tags and components
 * are now registry-aware. Threading it by hand touches hundreds of call sites; where the value is
 * genuinely just "the current world's registries", this looks it up instead.
 * </p><p>
 * <b>Only valid while a level is loaded.</b> Do not use it in registry/bootstrap code, in datagen,
 * or for anything crossing a dimension of meaning (a recipe codec must take its Provider properly).
 * Prefer a real parameter wherever one is already in scope - this is a migration aid, not a design.
 * </p>
 */
public class LegacyRegistries {

    /** The current server's registries, or the client's if this is a client with a level loaded. */
    public static HolderLookup.Provider access() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) return server.registryAccess();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            RegistryAccess client = ClientAccess.level();
            if (client != null) return client;
        };
        throw new IllegalStateException("No registry access: LegacyRegistries was used outside a loaded level");
    };

    public static CompoundTag serializeNBT(ItemStackHandler handler) {
        return handler.serializeNBT(access());
    };

    public static void deserializeNBT(ItemStackHandler handler, CompoundTag tag) {
        handler.deserializeNBT(access(), tag);
    };

    /** Split out so the client class is only loaded on a client. */
    private static class ClientAccess {
        private static RegistryAccess level() {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            return mc.level == null ? null : mc.level.registryAccess();
        };
    };

};
