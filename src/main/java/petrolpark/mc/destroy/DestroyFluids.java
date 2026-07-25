package petrolpark.mc.destroy;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import petrolpark.mc.destroy.chemistry.minecraft.MixtureFluid;
import petrolpark.mc.destroy.chemistry.minecraft.MixtureFluid.MixtureFluidType;

/**
 * PORT (1.21.1): only the Mixture Fluids so far. The rest of Destroy's fluids (urine, crude oil,
 * the potions, the molten metals and glass) need {@code ColoredFluidType}, the molten Fluid
 * classes and {@code DestroyTags.Fluids}, which are content and come back with it.
 */
public class DestroyFluids {

    public static final double AIR_MOLAR_DENSITY = 0.0420352380152d; // In moles/liter

    public static FluidStack air(int amount, float temperature) {
        return MixtureFluid.of(amount, MixtureFluid.airMixture(temperature), "fluid.destroy.air");
    };

    public static final FluidEntry<MixtureFluid> MIXTURE = REGISTRATE.virtualFluid("mixture",
        Destroy.asResource("fluid/mixture_still"),
        Destroy.asResource("fluid/mixture_flow"),
        MixtureFluidType::new,
        MixtureFluid::createSource,
        MixtureFluid::createFlowing
        ).register();

    public static final FluidEntry<MixtureFluid> GAS_MIXTURE = REGISTRATE.virtualFluid("gas", // For display purposes only
        Destroy.asResource("fluid/gas"),
        Destroy.asResource("fluid/gas"),
        MixtureFluidType::new,
        MixtureFluid::createSource,
        MixtureFluid::createFlowing
        ).register();

    public static boolean isMixture(FluidStack stack) {
        return stack != null && !stack.isEmpty() && isMixture(stack.getFluid()) && stack.has(DestroyDataComponents.MIXTURE);
    };

    public static boolean isMixture(Fluid fluid) {
        return fluid.isSame(MIXTURE.get());
    };

    public static void register() {};

};
