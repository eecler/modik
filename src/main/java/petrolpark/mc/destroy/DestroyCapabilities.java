package petrolpark.mc.destroy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import petrolpark.mc.destroy.core.chemistry.storage.MixtureStorageCapabilities;
import petrolpark.mc.destroy.core.explosion.mixedexplosive.MixedExplosiveCapabilities;

/**
 * PORT (1.21.1): Block Entities no longer answer {@code getCapability} themselves and Items no
 * longer build their own providers in {@code initCapabilities} - everything is registered up-front
 * against the type. Create does this with a {@code static registerCapabilities} per Block Entity
 * class; this is the single listener that calls them.
 * <p>
 * Add <b>one line</b> per cluster here and keep the actual registration in the cluster, so parallel
 * porting sessions do not collide in this file.
 * </p>
 */
public class DestroyCapabilities {

    public static final void register(IEventBus modEventBus) {
        modEventBus.addListener(DestroyCapabilities::registerCapabilities);
    };

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        MixtureStorageCapabilities.registerCapabilities(event);
        MixedExplosiveCapabilities.registerCapabilities(event);
    };

};
