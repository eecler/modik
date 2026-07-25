package petrolpark.mc.destroy;

import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DestroyPotions {
    
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, Destroy.MOD_ID);

    public static final RegistryObject<Potion> UNIQUE = POTIONS.register("unique", Potion::new);

    public static void register(IEventBus bus) {
        POTIONS.register(bus);
    };
};
