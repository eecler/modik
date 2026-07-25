package petrolpark.mc.destroy.config;

import net.createmod.catnip.config.ConfigBase;

public class DestroyCommonConfigs extends ConfigBase {

    public final ConfigBool enableBabyBlue = b(true, "enableBabyBlue", "Whether Baby Blue and its Reactions are enabled");
    public final ConfigBool enableAlcohol = b(true, "enableAlcohol", "Whether alcohol and its effects are enabled");

    @Override
    public String getName() {
        return "common";
    };

};
