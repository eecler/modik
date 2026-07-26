package petrolpark.mc.destroy.client;

import java.util.function.Supplier;

import com.simibubi.create.foundation.particle.ICustomParticleData;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import petrolpark.mc.destroy.Destroy;
import petrolpark.mc.destroy.core.chemistry.hazard.mobeffect.TearParticle;

/**
 * PORT (1.21.1): only the Particles reachable from ported content so far - the rest (confetti, gas,
 * rain, tinted splash) come back with theirs. The registry moved from {@code ForgeRegistries} to a
 * {@link Registries} key and {@code RegistryObject} became {@link DeferredHolder}; the
 * Create-derived entry class is otherwise unchanged.
 */
public enum DestroyParticleTypes {

    TEAR(TearParticle.Data::new),
    ;

    private final ParticleEntry<? extends ParticleOptions> particleEntry;

    <T extends ParticleOptions> DestroyParticleTypes(Supplier<? extends ICustomParticleData<T>> typeProvider) {
        particleEntry = new ParticleEntry<T>(Lang.asId(name()), typeProvider);
    };

    public static void register(IEventBus modEventBus) {
        ParticleEntry.PARTICLE_TYPES.register(modEventBus);
    };

    /**
     * Assign the right provider to every Particle Type. For some unfathomable reason these are
     * assigned in an event and not when the Particle Types themselves are registered.
     */
    @OnlyIn(Dist.CLIENT)
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        for (DestroyParticleTypes particleType : values()) {
            particleType.particleEntry.registerProvider(event);
        };
    };

    public ParticleType<?> get() {
        return particleEntry.object.get();
    };

    /**
     * Convenience class to register a Particle and also its provider when initialized.
     * Copied from the {@link com.simibubi.create.AllParticleTypes} source code.
     */
    private static class ParticleEntry<T extends ParticleOptions> {

        private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Destroy.MOD_ID);

        private final Supplier<? extends ICustomParticleData<T>> typeProvider;
        private final DeferredHolder<ParticleType<?>, ParticleType<T>> object;

        public ParticleEntry(String name, Supplier<? extends ICustomParticleData<T>> typeProvider) {
            this.typeProvider = typeProvider;
            object = PARTICLE_TYPES.register(name, () -> typeProvider.get().createType());
        };

        @OnlyIn(Dist.CLIENT)
        public void registerProvider(RegisterParticleProvidersEvent event) {
            typeProvider.get().register(object.get(), event);
        };

    };

};
