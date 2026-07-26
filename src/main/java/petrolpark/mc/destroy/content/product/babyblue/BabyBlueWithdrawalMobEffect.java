package petrolpark.mc.destroy.content.product.babyblue;

import petrolpark.mc.destroy.Destroy;
import petrolpark.mc.destroy.DestroyDamageSources;
import petrolpark.mc.destroy.DestroyMobEffects;
import petrolpark.mc.destroy.client.DestroyLang;
import petrolpark.mc.destroy.config.DestroySubstancesConfigs;
import petrolpark.mc.destroy.core.mobeffect.UncurableMobEffect;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * PORT (1.21.1): the addiction level (PlayerBabyBlueAddictionCapability, now an attachment)
 * returns with the capability pass; until then everyone takes withdrawal damage at the fixed
 * non-player rate. The Baby Blue Powder exemption returns with the item.
 */
@EventBusSubscriber(modid = Destroy.MOD_ID)
public class BabyBlueWithdrawalMobEffect extends UncurableMobEffect {

    public BabyBlueWithdrawalMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x91B1B7);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, Destroy.asResource("effect.baby_blue_withdrawal.movement_speed"), (double)-0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, Destroy.asResource("effect.baby_blue_withdrawal.attack_speed"), (double)-0.45F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, Destroy.asResource("effect.baby_blue_withdrawal.attack_damage"), (double)-1.0F, AttributeModifier.Operation.ADD_VALUE);
    };

    @Override
    @SuppressWarnings("null") // We know the effect isn't null if its ticking
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            int duration = livingEntity.getEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL).getDuration(); // This is the bit it says is null
            if (duration % 50 == 0) {
                livingEntity.hurt(DestroyDamageSources.babyBlueOverdose(livingEntity.level()), 1f);
            };
        };
        return super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    };

    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();

        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null && DestroySubstancesConfigs.babyBlueEnabled() && player.hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL) && !food.canAlwaysEat()) {
            player.displayClientMessage(DestroyLang.translate("tooltip.eating_prevented.baby_blue").component(), true);
            event.setCanceled(true);
        };
    };
};
