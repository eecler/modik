package petrolpark.mc.destroy.content.product.babyblue;

import petrolpark.mc.destroy.Destroy;
import petrolpark.mc.destroy.DestroyAdvancementTrigger;
import petrolpark.mc.destroy.DestroyMobEffects;
import petrolpark.mc.destroy.config.DestroyConfigs;
import petrolpark.mc.destroy.config.DestroySubstancesConfigs;
import petrolpark.mc.destroy.core.mobeffect.UncurableMobEffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * PORT (1.21.1): the addiction level (PlayerBabyBlueAddictionCapability, now an attachment)
 * returns with the capability pass; until then withdrawal uses the base (zero-addiction) length.
 */
@EventBusSubscriber(modid = Destroy.MOD_ID)
public class BabyBlueHighMobEffect extends UncurableMobEffect {

    public BabyBlueHighMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8BDCEB);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, Destroy.asResource("effect.baby_blue_high.movement_speed"), (double)0.3F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, Destroy.asResource("effect.baby_blue_high.attack_speed"), (double)0.9F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, Destroy.asResource("effect.baby_blue_high.attack_damage"), (double)2.0F, AttributeModifier.Operation.ADD_VALUE);
    };

    @Override
    @SuppressWarnings("null") // We know the effect isn't null if its ticking
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            int duration = livingEntity.getEffect(DestroyMobEffects.BABY_BLUE_HIGH).getDuration(); // This is the bit it says is null
            if (duration == 1) {
                // Apply the Baby Blue Withdrawal Effect as the BabyBlue High Effect runs out.
                if (livingEntity instanceof Player player) {
                    player.addEffect(new MobEffectInstance(DestroyMobEffects.BABY_BLUE_WITHDRAWAL, 10 * 20));
                };
            } else {
                livingEntity.removeEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL);
            };

            if (livingEntity instanceof Player player) {
                Level level = player.level();
                DestroyAdvancementTrigger.TAKE_BABY_BLUE.award(level, player);
            };

            if (livingEntity instanceof Animal animal && !animal.isBaby()) {
                if (animal.getAge() > 0) animal.resetLove();
                animal.setAge(0);
            };
        };

        return super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true; // Apply effects every tick
    };

    /**
     * Give the Player Haste/Mining Fatigue if they have Baby Blue High/Withdrawal respectively.
     */
    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!DestroySubstancesConfigs.babyBlueEnabled()) return;
        Player player = event.getEntity();
        if (player.hasEffect(DestroyMobEffects.BABY_BLUE_HIGH)) {
            event.setNewSpeed(event.getOriginalSpeed() + (DestroyConfigs.server().substances.babyBlueMiningSpeedBonus.getF() * (player.getEffect(DestroyMobEffects.BABY_BLUE_HIGH).getAmplifier() + 1))); // Increase Haste with Baby Blue High
        } else if (player.hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL)) {
            event.setNewSpeed(event.getOriginalSpeed() + (DestroyConfigs.server().substances.babyBlueWidthdrawalSpeedBonus.getF() * (player.getEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL).getAmplifier() + 1))); // Decrease Haste with Baby Blue Withdrawal
            if (event.getNewSpeed() <= 0f) event.setNewSpeed(0f);
        };
    };
};
