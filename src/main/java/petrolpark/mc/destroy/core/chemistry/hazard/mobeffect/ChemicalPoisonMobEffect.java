package petrolpark.mc.destroy.core.chemistry.hazard.mobeffect;

import petrolpark.mc.destroy.DestroyDamageSources;
import petrolpark.mc.destroy.DestroyMobEffects;
import petrolpark.mc.destroy.core.mobeffect.UncurableMobEffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * PORT (1.21.1): the poisoning molecule (EntityChemicalPoisonCapability, now an attachment) and
 * the tooltip naming it return with the capability pass; until then the damage source reports no
 * specific molecule.
 */
public class ChemicalPoisonMobEffect extends UncurableMobEffect {

    public ChemicalPoisonMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
    };

    @Override
    @SuppressWarnings("null") // We know the effect isn't null if its ticking
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            int duration = livingEntity.getEffect(DestroyMobEffects.CHEMICAL_POISON).getDuration(); // This is the bit it says is null
            if (duration % 50 == 0) {
                livingEntity.hurt(DestroyDamageSources.chemicalPoison(livingEntity.level(), null), 1f);
            };
        };
        return super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    };

};
