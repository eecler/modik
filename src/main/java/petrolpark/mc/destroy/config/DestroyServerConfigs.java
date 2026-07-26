package petrolpark.mc.destroy.config;

import net.createmod.catnip.config.ConfigBase;
import petrolpark.mc.destroy.core.pollution.PollutionConfigs;

public class DestroyServerConfigs extends ConfigBase {

    public final DestroyBlocksConfigs blocks = nested(0, DestroyBlocksConfigs::new, "Destroy's blocks");
    public final DestroyEquipmentConfigs equipment = nested(0, DestroyEquipmentConfigs::new, "Equipment");
    public final PollutionConfigs pollution = nested(0, PollutionConfigs::new, "Pollution");
    public final DestroySubstancesConfigs substances = nested(0, DestroySubstancesConfigs::new, "Drugs, medicines and alcohol");

    @Override
    public String getName() {
        return "server";
    };
    
};
