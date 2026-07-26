package petrolpark.mc.destroy.content.processing.moltenblock;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import petrolpark.mc.destroy.Destroy;
import petrolpark.mc.destroy.DestroyBlocks;

/**
 * The Molten Blocks and the Fluids and buckets which place them.
 * <p>
 * PORT (1.21.1): these live in their own holder rather than in {@link DestroyBlocks},
 * {@code DestroyFluids} and {@code DestroyItems} so that parallel porting sessions only ever add a
 * single line to the shared registries. All of their assets are hand-copied from 1.20.1, so datagen
 * is nooped for them.
 * </p>
 */
public class MoltenBlocks {

    public static final BlockEntry<MoltenStainlessSteelBlock> MOLTEN_STAINLESS_STEEL = REGISTRATE.block("molten_stainless_steel", MoltenStainlessSteelBlock::new)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .lightLevel(state -> 15)
            .noLootTable()
            .dynamicShape()
        ).tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .blockstate(NonNullBiConsumer.noop())
        .register();

    public static final BlockEntry<MoltenBorosilicateGlassBlock> MOLTEN_BOROSILICATE_GLASS = REGISTRATE.block("molten_borosilicate_glass", MoltenBorosilicateGlassBlock::new)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .lightLevel(state -> 15)
            .noLootTable()
            .dynamicShape()
        ).tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .blockstate(NonNullBiConsumer.noop())
        .register();

    public static final BlockEntry<FastCoolingMoltenPillarBlock> STAINLESS_STEEL_RODS = REGISTRATE.block("stainless_steel_rods_block", FastCoolingMoltenPillarBlock::new)
        .initialProperties(DestroyBlocks.STAINLESS_STEEL_BLOCK)
        .properties(p -> p
            .mapColor(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? MapColor.COLOR_ORANGE : MapColor.METAL)
            .lightLevel(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? 15 : 0)
        ).tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .item()
        .model(NonNullBiConsumer.noop())
        .build()
        .register();

    public static final BlockEntry<BorosilicateGlassFiberBlock> BOROSILICATE_GLASS_FIBER = REGISTRATE.block("borosilicate_glass_fiber", BorosilicateGlassFiberBlock::new)
        .initialProperties(MOLTEN_BOROSILICATE_GLASS)
        .properties(p -> p
            .mapColor(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? MapColor.COLOR_RED : MapColor.NONE)
            .lightLevel(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? 15 : 0)
            .sound(SoundType.WOOL)
        ).tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WOOL)
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .item()
        .model(NonNullBiConsumer.noop())
        .tag(ItemTags.WOOL)
        .build()
        .register();

    public static final FluidEntry<MoltenStainlessSteelFluid> MOLTEN_STAINLESS_STEEL_FLUID = REGISTRATE.virtualFluid("molten_stainless_steel",
        Destroy.asResource("block/molten_stainless_steel"),
        Destroy.asResource("block/molten_stainless_steel"),
        CreateRegistrate::defaultFluidType,
        MoltenStainlessSteelFluid::createSource,
        MoltenStainlessSteelFluid::createFlowing
        ).register();

    public static final FluidEntry<MoltenBorosilicateGlassFluid> MOLTEN_BOROSILICATE_GLASS_FLUID = REGISTRATE.virtualFluid("molten_borosilicate_glass",
        Destroy.asResource("block/molten_borosilicate_glass"),
        Destroy.asResource("block/molten_borosilicate_glass"),
        CreateRegistrate::defaultFluidType,
        MoltenBorosilicateGlassFluid::createSource,
        MoltenBorosilicateGlassFluid::createFlowing
        ).register();

    public static final ItemEntry<SolidBucketItem>

    MOLTEN_STAINLESS_STEEL_BUCKET = REGISTRATE.item("molten_stainless_steel_bucket", p -> new SolidBucketItem(MOLTEN_STAINLESS_STEEL.get(), SoundEvents.BUCKET_EMPTY_LAVA, p))
        .model(NonNullBiConsumer.noop())
        .register(),

    MOLTEN_BOROSILICATE_GLASS_BUCKET = REGISTRATE.item("molten_borosilicate_glass_bucket", p -> new SolidBucketItem(MOLTEN_BOROSILICATE_GLASS.get(), SoundEvents.BUCKET_EMPTY_LAVA, p))
        .model(NonNullBiConsumer.noop())
        .register();

    public static final void register() {};

};
