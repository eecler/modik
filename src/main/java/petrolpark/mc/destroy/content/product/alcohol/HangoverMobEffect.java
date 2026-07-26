package petrolpark.mc.destroy.content.product.alcohol;

import java.util.List;
import java.util.Set;

import petrolpark.mc.destroy.Destroy;
import petrolpark.mc.destroy.DestroyDamageSources;
import petrolpark.mc.destroy.DestroyMobEffects;
import petrolpark.mc.destroy.MoveToPetrolparkLibrary;
import petrolpark.mc.destroy.config.DestroyConfigs;
import petrolpark.mc.destroy.core.mobeffect.DestroyMobEffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

@MoveToPetrolparkLibrary
@EventBusSubscriber(modid = Destroy.MOD_ID)
public class HangoverMobEffect extends DestroyMobEffect {

    /** The Aspirin Syringe cures hangovers; the item invokes this cure when it returns. */
    public static final EffectCure ASPIRIN_CURE = EffectCure.get("destroy_aspirin");

    public HangoverMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x59390B);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, Destroy.asResource("effect.hangover.movement_speed"), (double)-0.10F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    };

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        cures.clear();
        cures.add(ASPIRIN_CURE);
    };

    @SubscribeEvent
    public static void onPlayerHearsSound(PlayLevelSoundEvent.AtPosition event) {
        if (event.getOriginalVolume() < DestroyConfigs.server().substances.soundSourceThresholds.get(event.getSource()).getF()) return;
        Vec3 pos = event.getPosition();
        float radius = DestroyConfigs.server().substances.hangoverNoiseTriggerRadius.getF();
        List<Entity> nearbyEntities = event.getLevel().getEntities(null, new AABB(pos.add(new Vec3(-radius,-radius,-radius)), pos.add(new Vec3(radius, radius, radius))));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(DestroyMobEffects.HANGOVER)) {
                    livingEntity.hurt(DestroyDamageSources.headache(livingEntity.level()), DestroyConfigs.server().substances.soundSourceDamage.get(event.getSource()).getF());
                };
            };
        };
    };
};
