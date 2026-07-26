package petrolpark.mc.destroy.core.explosion.mixedexplosive;

import petrolpark.mc.destroy.legacy.LegacyRegistries;
import petrolpark.mc.destroy.legacy.LegacyNBT;
import petrolpark.mc.destroy.core.explosion.mixedexplosive.ExplosiveProperties.ExplosivePropertyCondition;

import net.minecraft.world.item.ItemStack;

public interface IMixedExplosiveItem {
    
    public default MixedExplosiveInventory getExplosiveInventory(ItemStack stack) {
        if (!stack.getItem().equals(this)) return new MixedExplosiveInventory(getExplosiveInventorySize(), getApplicableExplosionConditions());
        MixedExplosiveInventory inventory = new MixedExplosiveInventory(getExplosiveInventorySize(), getApplicableExplosionConditions());
        LegacyRegistries.deserializeNBT(inventory, LegacyNBT.getOrCreateTag(stack).getCompound("ExplosiveMix"));
        return inventory;
    };

    public default void setExplosiveInventory(ItemStack stack, MixedExplosiveInventory inv) {
        if (inv != null && stack.getItem().equals(this)) LegacyNBT.getOrCreateTag(stack).put("ExplosiveMix", LegacyRegistries.serializeNBT(inv));
    };

    public int getExplosiveInventorySize();

    public ExplosivePropertyCondition[] getApplicableExplosionConditions();
};
