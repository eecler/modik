package petrolpark.mc.destroy;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * PORT (1.21.1): 1.20.5 replaced Item/Fluid Stack NBT with data components, so the Mixture a
 * Fluid Stack carries can no longer live in a {@code "Mixture"} child tag.
 * <p>
 * The Mixture is still serialized by {@link petrolpark.mc.destroy.chemistry.legacy.ReadOnlyMixture#writeNBT}
 * into a {@link CompoundTag}, so this component simply holds that tag. That keeps the whole
 * chemistry serialization untouched - only where the tag is stored has changed.
 * </p>
 */
public class DestroyDataComponents {

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Destroy.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> MIXTURE = DATA_COMPONENT_TYPES.register("mixture",
        () -> DataComponentType.<CompoundTag>builder()
            .persistent(CompoundTag.CODEC)
            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
            .build()
    );

    /**
     * PORT (1.21.1): where {@link petrolpark.mc.destroy.core.chemistry.storage.IMixtureStorageItem
     * Mixture storage Items} used to keep their contents in a {@code "Fluid"} child tag, they now
     * hold a Fluid Stack component. {@link SimpleFluidContent} is NeoForge's own holder for this.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = DATA_COMPONENT_TYPES.register("fluid_content",
        () -> DataComponentType.<SimpleFluidContent>builder()
            .persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
            .build()
    );

    /**
     * The Mixture a piece of protective equipment was splashed with, which will hurt whoever takes
     * it off without washing it first.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> CONTAMINATING_FLUID = DATA_COMPONENT_TYPES.register("contaminating_fluid",
        () -> DataComponentType.<SimpleFluidContent>builder()
            .persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
            .build()
    );

    public static final void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    };
};
