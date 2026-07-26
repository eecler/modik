package petrolpark.mc.destroy.core.chemistry.storage.measuringcylinder;

import petrolpark.mc.destroy.DestroyVoxelShapes;
import petrolpark.mc.destroy.config.DestroyConfigs;
import petrolpark.mc.destroy.core.chemistry.storage.MixtureStorage;
import petrolpark.mc.destroy.core.chemistry.storage.PlaceableMixtureTankBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MeasuringCylinderBlock extends PlaceableMixtureTankBlock<MeasuringCylinderBlockEntity> {

    public MeasuringCylinderBlock(Properties properties) {
        super(properties);
    };

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return DestroyVoxelShapes.MEASURING_CYLINDER;
    };

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        return MeasuringCylinderBlockItem.tryOpenTransferScreen(pLevel, pPos, pState, pHit.getDirection(), pPlayer, InteractionHand.MAIN_HAND, pPlayer.getItemInHand(InteractionHand.MAIN_HAND), dynamicShape);
    };

    @Override
    public int getMixtureCapacity() {
        return DestroyConfigs.server().blocks.measuringCylinderCapacity.get();
    };

    @Override
    public Class<MeasuringCylinderBlockEntity> getBlockEntityClass() {
        return MeasuringCylinderBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends MeasuringCylinderBlockEntity> getBlockEntityType() {
        return MixtureStorage.MEASURING_CYLINDER_BLOCK_ENTITY.get();
    };
    
};
