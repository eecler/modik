package petrolpark.mc.destroy.config;

import net.createmod.catnip.config.ConfigBase;
import petrolpark.mc.destroy.core.pollution.ClientPollutionConfigs;

public class DestroyClientConfigs extends ConfigBase {

    public final ClientPollutionConfigs pollution = nested(0, ClientPollutionConfigs::new, "Pollution");
    public final DestroyClientChemistryConfigs chemistry = nested(0, DestroyClientChemistryConfigs::new, "Chemistry");

    @Override
    public String getName() {
        return "client";
    };
    
};
