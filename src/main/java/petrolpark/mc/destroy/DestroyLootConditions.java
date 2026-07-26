package petrolpark.mc.destroy;

import petrolpark.mc.destroy.core.explosion.ObliterationCondition;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DestroyLootConditions {

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Destroy.MOD_ID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType>
    OBLITERATION = LOOT_CONDITIONS.register("obliteration", () -> new LootItemConditionType(ObliterationCondition.CODEC));

    public static void register(IEventBus eventBus) {
        LOOT_CONDITIONS.register(eventBus);
    };
};
