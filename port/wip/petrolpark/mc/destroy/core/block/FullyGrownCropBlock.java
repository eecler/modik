package petrolpark.mc.destroy.core.block;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import petrolpark.mc.destroy.DestroyVoxelShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FullyGrownCropBlock extends BushBlock {

    /**
     * PORT (1.21.1): {@link BushBlock} now demands a codec. Destroy's crops carry the Item they
     * drop as a seed, so the usual {@code simpleCodec} will not do.
     */
    public static final MapCodec<FullyGrownCropBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        propertiesCodec(),
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("seed").forGetter(b -> b.seed.get())
    ).apply(instance, (properties, seed) -> new FullyGrownCropBlock(properties, () -> seed)));

    private Supplier<? extends Item> seed;

    public FullyGrownCropBlock(Properties properties, Supplier<? extends Item> seed) {
        super(properties);
        this.seed = seed;
    };

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    };

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    };

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return (level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos)) && super.canSurvive(state, level, pos);
    };

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(seed.get());
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext pContext) {
        return DestroyVoxelShapes.CROP;
    };

    @Override
    public Item asItem() {
        return seed.get();
    };
    
};
