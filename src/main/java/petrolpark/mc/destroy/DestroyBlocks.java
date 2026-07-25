package petrolpark.mc.destroy;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

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

    public static final void register() {};

};
