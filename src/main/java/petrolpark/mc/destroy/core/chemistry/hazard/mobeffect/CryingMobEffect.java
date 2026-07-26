package petrolpark.mc.destroy.core.chemistry.hazard.mobeffect;

import petrolpark.mc.destroy.core.mobeffect.DestroyMobEffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * PORT (1.21.1): the tear particles (TearParticle + DestroyParticleTypes) and the client-sync
 * packet (CryingS2CPacket via DestroyMessages) return with the particle and packet passes; until
 * then the effect applies but has no visuals.
 */
public class CryingMobEffect extends DestroyMobEffect {

    public CryingMobEffect() {
        super(MobEffectCategory.NEUTRAL, 0xCBF2F0);
    };

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        return super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    };

};
