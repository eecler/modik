package petrolpark.mc.destroy;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.Tags;
import petrolpark.mc.destroy.content.processing.sieve.MechanicalSieveBlock;

/**
 * PORT (1.21.1): only the blocks reachable from ported code so far. The 1.20.1 class is over a
 * thousand lines of machines (Vats, the Pumpjack, the Centrifuge, Bubble Caps...) which come back
 * with their Block Entities and renderers.
 */
public class DestroyBlocks {

    public static final BlockEntry<Block> CORDITE_BLOCK = REGISTRATE.block("cordite_block", Block::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).tag(BlockTags.MINEABLE_WITH_SHOVEL)
        .simpleItem()
        .register();

    // The blockstate, models and loot table are hand-copied from 1.20.1, so datagen is nooped for them.
    // Stress impact is hardcoded until DestroyStressConfigs is ported.
    public static final BlockEntry<MechanicalSieveBlock> MECHANICAL_SIEVE = REGISTRATE.block("mechanical_sieve", MechanicalSieveBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .transform(TagGen.axeOrPickaxe())
        .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 0.5d))
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .item()
        .model(NonNullBiConsumer.noop())
        .build()
        .register();

    // The materials the Molten Blocks solidify into. Shared, so they live here rather than with the
    // Molten Blocks themselves; their assets are hand-copied from 1.20.1, so datagen is nooped.
    public static final BlockEntry<CasingBlock> STAINLESS_STEEL_BLOCK = REGISTRATE.block("stainless_steel_block", CasingBlock::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .sound(SoundType.COPPER)
            .strength(7f, 8f)
        ).transform(TagGen.pickaxeOnly())
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(DestroySpriteShifts.STAINLESS_STEEL_BLOCK)))
        .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.makeCasing(block, DestroySpriteShifts.STAINLESS_STEEL_BLOCK)))
        .tag(BlockTags.NEEDS_STONE_TOOL, BlockTags.BEACON_BASE_BLOCKS, Tags.Blocks.STORAGE_BLOCKS, DestroyTags.commonBlockTag("storage_blocks/stainless_steel"))
        .item()
        .model(NonNullBiConsumer.noop())
        .tag(Tags.Items.STORAGE_BLOCKS, DestroyTags.commonItemTag("storage_blocks/stainless_steel"))
        .build()
        .register();

    public static final BlockEntry<TransparentBlock> BOROSILICATE_GLASS = REGISTRATE.block("borosilicate_glass", TransparentBlock::new)
        .initialProperties(() -> Blocks.GLASS)
        .properties(p -> p
            .strength(2f)
        ).tag(Tags.Blocks.GLASS_BLOCKS, Tags.Blocks.GLASS_BLOCKS_COLORLESS, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .onRegister(CreateRegistrate.connectedTextures(() -> new SimpleCTBehaviour(DestroySpriteShifts.BOROSILICATE_GLASS)))
        .item()
        .model(NonNullBiConsumer.noop())
        .tag(Tags.Items.GLASS_BLOCKS, Tags.Items.GLASS_BLOCKS_COLORLESS)
        .build()
        .register();

    public static final void register() {};

};
