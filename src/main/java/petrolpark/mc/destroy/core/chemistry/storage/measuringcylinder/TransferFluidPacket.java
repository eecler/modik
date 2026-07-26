package petrolpark.mc.destroy.core.chemistry.storage.measuringcylinder;

import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import petrolpark.mc.destroy.DestroyPackets;
import petrolpark.mc.destroy.core.chemistry.storage.IMixtureStorageItem;
import petrolpark.mc.destroy.core.chemistry.storage.ItemMixtureTank;

/**
 * How much Mixture the Player chose to move between a Measuring Cylinder and the tank it was
 * clicked on.
 * <p>
 * PORT (1.21.1): was {@code TransferFluidC2SPacket}. The hand was hand-encoded as a boolean because
 * 1.20.1's {@code FriendlyByteBuf} had no enum Stream Codec; {@code ByteBufCodecs} does, so the
 * Interaction Hand travels as itself.
 * </p>
 */
public record TransferFluidPacket(BlockPos pos, Direction face, InteractionHand hand, boolean blockToItem, int transferAmount) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, TransferFluidPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, TransferFluidPacket::pos,
        Direction.STREAM_CODEC, TransferFluidPacket::face,
        ByteBufCodecs.idMapper(i -> InteractionHand.values()[i], InteractionHand::ordinal), TransferFluidPacket::hand,
        ByteBufCodecs.BOOL, TransferFluidPacket::blockToItem,
        ByteBufCodecs.VAR_INT, TransferFluidPacket::transferAmount,
        TransferFluidPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return DestroyPackets.TRANSFER_FLUID;
    };

    @Override
    public void handle(ServerPlayer player) {
        ItemStack stack = player.getItemInHand(hand());
        if (!(stack.getItem() instanceof IMixtureStorageItem mixtureItem)) return;
        if (!(stack.getCapability(Capabilities.FluidHandler.ITEM) instanceof ItemMixtureTank itemTank)) return;
        Level level = player.level();
        BlockState state = level.getBlockState(pos());
        IFluidHandler otherTank = mixtureItem.getTank(level, pos(), state, face(), player, hand(), stack, !blockToItem());
        if (otherTank == null) return;
        if (blockToItem()) {
            mixtureItem.afterFill(level, pos(), state, face(), player, hand(), stack, mixtureItem.tryFill(stack, itemTank, otherTank, transferAmount()));
        } else {
            mixtureItem.afterEmpty(level, pos(), state, face(), player, hand(), stack, mixtureItem.tryEmpty(stack, itemTank, otherTank, false, transferAmount()));
        };
    };

};
