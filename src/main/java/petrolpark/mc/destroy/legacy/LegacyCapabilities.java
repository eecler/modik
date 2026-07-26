package petrolpark.mc.destroy.legacy;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 1.20.1-style capability lookups over the 1.21.1 NeoForge capability system.
 * <p>
 * Replaces the {@code blockEntity.getCapability(ForgeCapabilities.X, side).resolve()} pattern.
 * {@link Optional} stands in for {@code LazyOptional}: {@code ifPresent}/{@code map}/
 * {@code orElse} chains keep working, but there is no invalidation - lookups are cheap in
 * NeoForge, so cached {@code LazyOptional} fields should just become direct calls to these.
 * </p>
 */
public class LegacyCapabilities {

    public static Optional<IItemHandler> getItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
        return Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side));
    };

    public static Optional<IFluidHandler> getFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
        return Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side));
    };

    public static Optional<IItemHandler> getItemHandler(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.ItemHandler.ITEM));
    };

    public static Optional<IFluidHandlerItem> getFluidHandlerItem(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM));
    };

};
