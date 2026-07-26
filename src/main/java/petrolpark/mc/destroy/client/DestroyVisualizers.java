package petrolpark.mc.destroy.client;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import petrolpark.mc.destroy.DestroyBlockEntityTypes;
import petrolpark.mc.destroy.content.processing.sieve.MechanicalSieveVisual;
import petrolpark.mc.destroy.content.processing.treetap.TreeTapRegistry;
import petrolpark.mc.destroy.content.processing.treetap.TreeTapVisual;

/**
 * Flywheel visuals for Destroy's block entities. On 1.20.1 these were attached with
 * {@code .visual(...)} on the {@code CreateRegistrate} block entity builders; the Registrate here
 * is the Library's, which has no such method, so they are registered with Flywheel directly.
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class DestroyVisualizers {

    @SubscribeEvent
    public static void register(FMLClientSetupEvent event) {
        DestroyPartials.init(); // Classloads the partial models before the first model bake

        SimpleBlockEntityVisualizer.builder(DestroyBlockEntityTypes.MECHANICAL_SIEVE.get())
            .factory(MechanicalSieveVisual::new)
            .apply();

        SimpleBlockEntityVisualizer.builder(TreeTapRegistry.TREE_TAP_BLOCK_ENTITY.get())
            .factory(TreeTapVisual::new)
            .apply();
    };

};
