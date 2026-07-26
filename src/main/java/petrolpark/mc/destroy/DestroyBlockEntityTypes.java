package petrolpark.mc.destroy;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import petrolpark.mc.destroy.content.processing.sieve.MechanicalSieveBlockEntity;
import petrolpark.mc.destroy.content.processing.sieve.MechanicalSieveRenderer;

/**
 * PORT (1.21.1): only the block entities reachable from ported content so far. The Registrate here
 * is not a {@code CreateRegistrate}, so there is no {@code .visual(...)} on the builder - Flywheel
 * visuals are registered in {@link petrolpark.mc.destroy.client.DestroyVisualizers} instead.
 */
public class DestroyBlockEntityTypes {

    public static final BlockEntityEntry<MechanicalSieveBlockEntity> MECHANICAL_SIEVE = REGISTRATE
        .blockEntity("mechanical_sieve", MechanicalSieveBlockEntity::new)
        .validBlock(DestroyBlocks.MECHANICAL_SIEVE)
        .renderer(() -> MechanicalSieveRenderer::new)
        .register();

    public static final void register() {};

};
