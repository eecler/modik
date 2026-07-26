package petrolpark.mc.destroy.core.mobeffect;

import java.util.Set;

import petrolpark.mc.destroy.MoveToPetrolparkLibrary;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

@MoveToPetrolparkLibrary
public class UncurableMobEffect extends DestroyMobEffect {
    public UncurableMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    };

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        cures.clear();
    };
};
