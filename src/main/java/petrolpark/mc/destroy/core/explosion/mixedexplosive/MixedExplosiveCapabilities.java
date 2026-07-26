package petrolpark.mc.destroy.core.explosion.mixedexplosive;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import petrolpark.mc.destroy.DestroyBlockEntityTypes;

/**
 * PORT (1.21.1): {@code SimpleDyeableNameableMixedExplosiveBlockEntity#getCapability} is gone -
 * Block Entity capabilities are now registered up-front against the type.
 * Called from {@link petrolpark.mc.destroy.DestroyCapabilities}.
 */
public class MixedExplosiveCapabilities {

    public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DestroyBlockEntityTypes.CUSTOM_EXPLOSIVE_MIX.get(), (be, side) -> be.getExplosiveInventory());
    };

};
