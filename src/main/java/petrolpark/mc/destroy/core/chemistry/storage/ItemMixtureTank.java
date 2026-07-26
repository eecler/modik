package petrolpark.mc.destroy.core.chemistry.storage;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import petrolpark.mc.destroy.DestroyDataComponents;
import petrolpark.mc.destroy.DestroyFluids;
import petrolpark.mc.destroy.core.fluid.GeniusFluidTankBehaviour.GeniusFluidTank;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

/**
 * The Fluid Handler in which {@link petrolpark.mc.destroy.core.chemistry.storage.IMixtureStorageItem IMixtureStorageItems} store Mixtures.
 * <p>
 * PORT (1.21.1): Items no longer provide their own Capabilities - this is handed out by
 * {@link MixtureStorageCapabilities}, and the contents live in a
 * {@link DestroyDataComponents#FLUID_CONTENT} component rather than a {@code "Fluid"} NBT tag.
 * </p>
 */
public class ItemMixtureTank extends GeniusFluidTank implements IFluidHandlerItem {

    private final ItemStack container;

    public ItemMixtureTank(ItemStack container, Consumer<FluidStack> updateCallback) {
        super(0, updateCallback);
        if (!(container.getItem() instanceof IMixtureStorageItem item)) throw new IllegalArgumentException(container.getItem().getDescriptionId()+" cannot store Mixtures");
        capacity = item.getCapacity(container);
        this.container = container;
        fluid = container.getOrDefault(DestroyDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy();
    };

    public ItemMixtureTank(ItemStack container) {
        this(container, f -> {});
    };

    public int getRemainingSpace() {
        return capacity - getFluidAmount();
    };

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        save();
    };

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        save();
    };

    private void save() {
        if (fluid.isEmpty()) {
            container.remove(DestroyDataComponents.FLUID_CONTENT);
        } else {
            container.set(DestroyDataComponents.FLUID_CONTENT, SimpleFluidContent.copyOf(fluid));
        };
    };

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return DestroyFluids.isMixture(fluid);
    };

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    };

};
