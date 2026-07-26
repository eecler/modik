package petrolpark.mc.destroy.core.chemistry.storage;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import petrolpark.mc.destroy.core.chemistry.storage.testtube.TestTubeRackBlockEntity;

/**
 * PORT (1.21.1): {@code IForgeItem#initCapabilities} is gone - Item Capabilities are now registered
 * up-front against the Item, not created per-Stack by the Item itself. Every
 * {@link IMixtureStorageItem} wants exactly the same provider, so rather than listing them (and
 * having to remember to add new ones), this collects them from the Item registry, which is frozen
 * by the time this event fires.
 * <p>
 * Called from {@link petrolpark.mc.destroy.DestroyCapabilities}.
 * </p>
 */
public class MixtureStorageCapabilities {

    public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof IMixtureStorageItem)) continue;
            event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new ItemMixtureTank(stack), item);
        };

        for (var type : MixtureStorage.MIXTURE_TANKS) {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type.get(), (be, side) -> be.getTank().getCapability());
        };

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MixtureStorage.TEST_TUBE_RACK_BLOCK_ENTITY.get(), (be, side) -> be.getInventory());
    };

};
