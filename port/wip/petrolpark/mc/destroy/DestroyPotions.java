package petrolpark.mc.destroy;

import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyPotions {
    
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Destroy.MOD_ID);

    public static final RegistryObject<Potion> UNIQUE = POTIONS.register("unique", Potion::new);

    public static void register(IEventBus bus) {
        POTIONS.register(bus);
    };
};
