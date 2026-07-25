package petrolpark.mc.destroy.core.registrate;

import com.mojang.datafixers.util.Function3;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.simibubi.create.foundation.gui.AllIcons;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import petrolpark.mc.destroy.DestroyRegistries;
import petrolpark.mc.destroy.core.pollution.PollutionType;
import petrolpark.mc.library.core.registrate.AbstractPetrolparkRegistrate;

public abstract class AbstractDestroyRegistrate<R extends AbstractPetrolparkRegistrate<R>> extends AbstractPetrolparkRegistrate<R> {

    protected AbstractDestroyRegistrate(String modid) {
        super(modid);
    };

    /**
     * Register a virtual Fluid - one which never exists as a block in the world.
     * <p>
     * PORT (1.21.1): on 1.20.1 Destroy's Registrate <em>was</em> a {@code CreateRegistrate}, so
     * this came for free. Here it extends the Library's Registrate instead, and the Library only
     * offers a simpler {@code virtualFluid(String, ItemLike)} which cannot take a custom Fluid
     * class or Fluid Type - both of which Mixtures need. Create's {@link VirtualFluidBuilder}
     * accepts any {@code AbstractRegistrate}, so it is used directly.
     * </p>
     */
    public <T extends BaseFlowingFluid> FluidBuilder<T, R> virtualFluid(String name, ResourceLocation stillTexture, ResourceLocation flowingTexture, FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return entry(name, c -> new VirtualFluidBuilder<T, R>(this, self(), name, c, stillTexture, flowingTexture, typeFactory, sourceFactory, flowingFactory));
    };

    public <POLLUTION_TYPE extends PollutionType<Level>> RegistryEntry<PollutionType<Level>, POLLUTION_TYPE> levelPollutionType(String name, AllIcons icon, Function3<Boolean, AllIcons, String, POLLUTION_TYPE> factory) {
        return simple(name, DestroyRegistries.Keys.LEVEL_POLLUTION_TYPE, () -> factory.apply(false, icon, Util.makeDescriptionId("pollution_type", ResourceLocation.fromNamespaceAndPath(getModid(), name))));
    };

    public <POLLUTION_TYPE extends PollutionType<ChunkAccess>> RegistryEntry<PollutionType<ChunkAccess>, POLLUTION_TYPE> chunkPollutionType(String name, AllIcons icon, Function3<Boolean, AllIcons, String, POLLUTION_TYPE> factory) {
        return simple(name, DestroyRegistries.Keys.CHUNK_POLLUTION_TYPE, () -> factory.apply(true, icon, Util.makeDescriptionId("pollution_type", ResourceLocation.fromNamespaceAndPath(getModid(), name))));
    };
    
};
