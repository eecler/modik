package petrolpark.mc.destroy.content.processing.treetap;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * The Tree Tap.
 * <p>
 * PORT (1.21.1): registration lives here rather than in the shared registries so that parallel
 * porting sessions only add a single line to them. Assets are hand-copied from 1.20.1, so datagen is
 * nooped. The Fluid Handler is no longer exposed by overriding {@code getCapability} on the Block
 * Entity - NeoForge wants it registered against the Block Entity type, which is what
 * {@link #registerCapabilities(RegisterCapabilitiesEvent)} does.
 * </p>
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class TreeTapRegistry {

    public static final BlockEntry<TreeTapBlock> TREE_TAP = REGISTRATE.block("tree_tap", TreeTapBlock::new)
        .initialProperties(AllBlocks.DEPLOYER)
        .transform(TagGen.axeOrPickaxe())
        .blockstate(NonNullBiConsumer.noop())
        .loot(NonNullBiConsumer.noop())
        .item()
        .model(NonNullBiConsumer.noop())
        .build()
        .register();

    public static final BlockEntityEntry<TreeTapBlockEntity> TREE_TAP_BLOCK_ENTITY = REGISTRATE
        .blockEntity("tree_tap", TreeTapBlockEntity::new)
        .validBlock(TREE_TAP)
        .renderer(() -> TreeTapRenderer::new)
        .register();

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TREE_TAP_BLOCK_ENTITY.get(), TreeTapBlockEntity::getFluidHandler);
    };

    public static final void register() {};

};
