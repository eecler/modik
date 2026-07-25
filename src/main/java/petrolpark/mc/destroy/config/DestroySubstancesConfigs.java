package petrolpark.mc.destroy.config;

/**
 * Feature switches for Destroy's substances.
 * <p>
 * PORT (1.21.1): the 1.20.1 class was a whole config section (Baby Blue addiction levels,
 * alcohol tuning, sodium decay and so on). Only these switches are reachable from ported code
 * so far - {@code DestroyReactions} gates the Baby Blue reactions on
 * {@link #babyBlueEnabled()} - so the rest returns with the content that reads it.
 * </p>
 */
public class DestroySubstancesConfigs {

    public static boolean babyBlueEnabled() {
        return DestroyConfigs.common().enableBabyBlue.get();
    };

    public static boolean alcoholEnabled() {
        return DestroyConfigs.common().enableAlcohol.get();
    };

};
