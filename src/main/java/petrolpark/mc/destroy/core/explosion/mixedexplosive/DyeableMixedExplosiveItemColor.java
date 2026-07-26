package petrolpark.mc.destroy.core.explosion.mixedexplosive;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class DyeableMixedExplosiveItemColor implements ItemColor {

    public static final DyeableMixedExplosiveItemColor INSTANCE = new DyeableMixedExplosiveItemColor();

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) return -1;
        return DyedItemColor.getOrDefault(stack, 0xFFFFFF);
    };

};
