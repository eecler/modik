package petrolpark.mc.destroy;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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

    public static final void register() {};

};
