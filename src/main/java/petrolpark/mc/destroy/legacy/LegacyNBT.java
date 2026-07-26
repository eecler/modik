package petrolpark.mc.destroy.legacy;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 1.20.1-style ItemStack NBT access over 1.21.1 Data Components ({@code minecraft:custom_data}).
 * <p>
 * The one semantic difference from 1.20.1: components are immutable, so the tags returned here are
 * COPIES. Mutating a returned tag does nothing until it is written back with {@link #setTag} -
 * call sites that relied on mutating the live tag must use {@link #update} instead. The rewrite
 * script flags every call site it converts so these can be reviewed.
 * </p>
 */
public class LegacyNBT {

    /** 1.20.1 {@code stack.getTag()}: null if the stack has no custom data. Returns a copy. */
    @Nullable
    public static CompoundTag getTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    };

    /** 1.20.1 {@code stack.getOrCreateTag()}. Returns a copy; write back with {@link #setTag}. */
    public static CompoundTag getOrCreateTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    };

    /** 1.20.1 {@code stack.hasTag()}. */
    public static boolean hasTag(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    };

    /** 1.20.1 {@code stack.setTag(tag)}. Null or empty removes the component. */
    public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        };
    };

    /** Read-modify-write in one step - the replacement for mutating a live 1.20.1 tag. */
    public static void update(ItemStack stack, Consumer<CompoundTag> mutator) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, mutator);
    };

    // FluidStack overloads - 1.20.1 Forge FluidStacks carried NBT too. The rewrite script cannot
    // tell receivers apart, so the compiler picks the right overload instead.

    @Nullable
    public static CompoundTag getTag(FluidStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    };

    public static CompoundTag getOrCreateTag(FluidStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    };

    public static boolean hasTag(FluidStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    };

    public static void setTag(FluidStack stack, @Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        };
    };

};
