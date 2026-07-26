package petrolpark.mc.destroy.core.chemistry.storage;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import java.util.List;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import petrolpark.mc.destroy.DestroyBlocks;
import petrolpark.mc.destroy.DestroyTags;
import petrolpark.mc.destroy.DestroyVoxelShapes;
import petrolpark.mc.destroy.config.DestroyConfigs;
import petrolpark.mc.destroy.core.chemistry.storage.SimpleMixtureTankBlockEntity.SimplePlaceableMixtureTankBlockEntity;
import petrolpark.mc.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlock;
import petrolpark.mc.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlockEntity;
import petrolpark.mc.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlockItem;
import petrolpark.mc.destroy.core.chemistry.storage.testtube.TestTubeItem;
import petrolpark.mc.destroy.core.chemistry.storage.testtube.TestTubeRackBlock;
import petrolpark.mc.destroy.core.chemistry.storage.testtube.TestTubeRackBlockEntity;
import petrolpark.mc.destroy.core.chemistry.storage.testtube.TestTubeRackRenderer;

/**
 * The vessels Mixtures can be carried and left lying around in: Test Tubes and their Rack, Beakers,
 * Round-Bottomed Flasks and the Measuring Cylinder.
 * <p>
 * PORT (1.21.1): these live in their own holder rather than in {@link DestroyBlocks},
 * {@code DestroyItems} and {@code DestroyBlockEntityTypes} so that parallel porting sessions only
 * ever add a single line to the shared registries - the same arrangement as
 * {@link petrolpark.mc.destroy.content.processing.moltenblock.MoltenBlocks}. Assets are copied from
 * 1.20.1, so datagen is nooped where it would overwrite them.
 * </p>
 */
public class MixtureStorage {

    // Blocks

    public static final BlockEntry<MeasuringCylinderBlock> MEASURING_CYLINDER = REGISTRATE.block("measuring_cylinder", MeasuringCylinderBlock::new)
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop()) // PlaceableMixtureTankBlock drops itself with its contents
        .item(MeasuringCylinderBlockItem::new)
        .properties(p -> p.stacksTo(1))
        .model(NonNullBiConsumer.noop())
        .build()
        .register();

    public static final BlockEntry<TestTubeRackBlock> TEST_TUBE_RACK = REGISTRATE.block("test_tube_rack", TestTubeRackBlock::new)
        .initialProperties(() -> Blocks.OAK_PLANKS)
        .tag(BlockTags.MINEABLE_WITH_AXE)
        .blockstate(NonNullBiConsumer.noop())
        .item()
        .model(NonNullBiConsumer.noop())
        .build()
        .register();

    public static final BlockEntry<SimplePlaceableMixtureTankBlock>

    BEAKER = REGISTRATE.block("beaker", SimplePlaceableMixtureTankBlock.of(() -> DestroyConfigs.server().blocks.beakerCapacity.get(), 5.5f, 0.5f, 5.5f, 10.5f, 7f, 10.5f, DestroyVoxelShapes.BEAKER))
        .initialProperties(MEASURING_CYLINDER)
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .item(SimplePlaceableMixtureTankBlockItem::new)
        .model(NonNullBiConsumer.noop())
        .build()
        .register(),

    ROUND_BOTTOMED_FLASK = REGISTRATE.block("round_bottomed_flask", SimplePlaceableMixtureTankBlock.of(() -> DestroyConfigs.server().blocks.roundBottomedFlaskCapacity.get(), 5.5f, 0.5f, 5.5f, 10.5f, 4.5f, 10.5f, DestroyVoxelShapes.ROUND_BOTTOMED_FLASK))
        .initialProperties(BEAKER)
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .item(SimplePlaceableMixtureTankBlockItem::new)
        .model(NonNullBiConsumer.noop())
        .build()
        .register();

    // Items

    public static final ItemEntry<TestTubeItem> TEST_TUBE = REGISTRATE.item("test_tube", TestTubeItem::new)
        .properties(p -> p.stacksTo(1))
        .tag(DestroyTags.Items.TEST_TUBE_RACK_STORABLE.tag)
        .model(NonNullBiConsumer.noop())
        .register();

    // Block Entities

    public static final BlockEntityEntry<SimplePlaceableMixtureTankBlockEntity> SIMPLE_MIXTURE_TANK = REGISTRATE
        .blockEntity("simple_mixture_tank", SimplePlaceableMixtureTankBlockEntity::new)
        .validBlocks(BEAKER, ROUND_BOTTOMED_FLASK)
        .renderer(() -> SimpleMixtureTankRenderer::new)
        .register();

    public static final BlockEntityEntry<MeasuringCylinderBlockEntity> MEASURING_CYLINDER_BLOCK_ENTITY = REGISTRATE
        .blockEntity("measuring_cylinder", MeasuringCylinderBlockEntity::new)
        .validBlock(MEASURING_CYLINDER)
        .renderer(() -> SimpleMixtureTankRenderer::new)
        .register();

    public static final BlockEntityEntry<TestTubeRackBlockEntity> TEST_TUBE_RACK_BLOCK_ENTITY = REGISTRATE
        .blockEntity("test_tube_rack", TestTubeRackBlockEntity::new)
        .validBlocks(TEST_TUBE_RACK)
        .renderer(() -> TestTubeRackRenderer::new)
        .register();

    /**
     * Every Block Entity that hands out a Mixture tank as its Fluid Handler Capability.
     * @see MixtureStorageCapabilities
     */
    public static final List<BlockEntityEntry<? extends SimpleMixtureTankBlockEntity>> MIXTURE_TANKS = List.of(
        SIMPLE_MIXTURE_TANK, MEASURING_CYLINDER_BLOCK_ENTITY
    );

    public static final void register() {};

};
