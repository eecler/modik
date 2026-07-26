package petrolpark.mc.destroy.core.chemistry.hazard;

import petrolpark.mc.destroy.DestroyPollutionTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import petrolpark.mc.destroy.Destroy;
import petrolpark.mc.destroy.DestroyDataComponents;
import petrolpark.mc.destroy.DestroyDamageSources;
import petrolpark.mc.destroy.DestroyFluids;
import petrolpark.mc.destroy.DestroyItems;
import petrolpark.mc.destroy.DestroyMobEffects;
import petrolpark.mc.destroy.DestroyTags.Items;
import petrolpark.mc.destroy.chemistry.legacy.LegacyElement;
import petrolpark.mc.destroy.chemistry.legacy.LegacySpecies;
import petrolpark.mc.destroy.chemistry.legacy.ReadOnlyMixture;
import petrolpark.mc.destroy.chemistry.legacy.index.DestroyMolecules;
import petrolpark.mc.destroy.client.DestroyLang;
import petrolpark.mc.destroy.config.DestroySubstancesConfigs;
import petrolpark.mc.destroy.core.pollution.PollutionType;
import petrolpark.mc.destroy.core.pollution.PollutionHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Destroy.MOD_ID)
public class ChemistryHazardHelper {
  
    /**
     * Apply the effects of exposure to a Mixture to an Entity.
     * @param level
     * @param entity
     * @param mixture
     * @param skinContact Whether the entity has contact with the Mixture on a body part other than their mouth (if they are "submerged" in it), regardless of any protective clothing
     */
    public static void damage(Level level, LivingEntity entity, FluidStack stack, boolean skinContact) {
        if (!DestroyFluids.isMixture(stack)) return;
        CompoundTag mixtureTag = stack.get(DestroyDataComponents.MIXTURE);
        if (mixtureTag == null) return;
        ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, mixtureTag);
        if (mixture.isEmpty()) return;

        boolean burning = mixture.getConcentrationOf(DestroyMolecules.PROTON) > 0.01f || mixture.getConcentrationOf(DestroyMolecules.HYDROXIDE) > 0.01f;
        boolean smelly = false;
        boolean carcinogen = false;
        boolean lacrimator = false;
        boolean lead = false;
        LegacySpecies toxicMolecule = null;

        for (LegacySpecies molecule : mixture.getContents(true)) {
            if (molecule.hasTag(DestroyMolecules.Tags.ACUTELY_TOXIC)) toxicMolecule = molecule;
            if (molecule.hasTag(DestroyMolecules.Tags.SMELLY)) smelly = true;
            if (molecule.hasTag(DestroyMolecules.Tags.CARCINOGEN)) carcinogen = true;
            if (molecule.hasTag(DestroyMolecules.Tags.LACRIMATOR)) lacrimator = true;
            if (molecule.getMolecularFormula().containsKey(LegacyElement.LEAD)) lead = true;
            if (toxicMolecule != null && smelly && carcinogen && lacrimator && lead) break;
        };

        boolean noseProtected = Protection.NOSE.isProtected(entity);
        boolean mouthProtected = Protection.MOUTH.isProtected(entity);
        boolean eyesProtected = Protection.EYES.isProtected(entity);
        boolean sensitivePartsProtected = noseProtected && mouthProtected && eyesProtected;
        boolean wholeBodyProtected = sensitivePartsProtected && Protection.HEAD.isProtected(entity) && Protection.BODY.isProtected(entity) && Protection.LEGS.isProtected(entity) && Protection.FEET.isProtected(entity);

        // Wah wah cry like a little baby
        if (lacrimator && !eyesProtected) {
            entity.addEffect(new MobEffectInstance(DestroyMobEffects.CRYING, 600, 0, false, false, true));
        };
        
        // Smelly chemicals
        if (smelly && !noseProtected && !(entity instanceof Player player && player.isCreative())) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false));
        };

        // Acutely toxic Molecules
        if (toxicMolecule != null && !sensitivePartsProtected && !level.isClientSide()) {
            EntityChemicalPoison.setMolecule(entity, toxicMolecule);
            if (!entity.hasEffect(DestroyMobEffects.CHEMICAL_POISON)) entity.addEffect(new MobEffectInstance(DestroyMobEffects.CHEMICAL_POISON, 219, 0, false, false));
        };
        
        // Carcinogens
        if (carcinogen && (!noseProtected || !mouthProtected)) {
            if (entity.getRandom().nextInt(2400) == 0) entity.addEffect(DestroyMobEffects.cancerInstance());
        };

        // Lead poisoning
        if (lead && (!noseProtected || !mouthProtected)) {
            if (entity.getRandom().nextInt(2400) == 0) DestroyMobEffects.increaseEffectLevel(entity, DestroyMobEffects.LEAD_POISONING, 1, -1);
        };

        // Acid/base burns
        if (skinContact) {
            if (wholeBodyProtected && (burning || smelly || carcinogen || toxicMolecule != null)) { // If there is a hazard
                for (ItemStack armor : entity.getArmorSlots()) contaminate(armor, stack);
            } else {
                if (burning) {
                    entity.hurt(DestroyDamageSources.chemicalBurn(level), 5f);
                };
            };
        };

    };
    
    public static void contaminate(ItemStack stack, FluidStack fluidStack) {
        if (!Items.CONTAMINABLE.matches(stack.getItem())) return;
        if (stack.has(DestroyDataComponents.CONTAMINATING_FLUID)) return; // Don't replace existing contamination
        stack.set(DestroyDataComponents.CONTAMINATING_FLUID, SimpleFluidContent.copyOf(fluidStack));
    };

    public static void decontaminate(ItemStack stack) {
        stack.remove(DestroyDataComponents.CONTAMINATING_FLUID);
    };

    /** The Mixture contaminating the given Stack, or an empty Stack if it is clean. */
    public static FluidStack getContamination(ItemStack stack) {
        return stack.getOrDefault(DestroyDataComponents.CONTAMINATING_FLUID, SimpleFluidContent.EMPTY).copy();
    };

    public static enum Protection {
        FEET(EquipmentSlot.FEET, Items.CHEMICAL_PROTECTION_FEET),
        LEGS(EquipmentSlot.LEGS, Items.CHEMICAL_PROTECTION_LEGS),
        BODY(EquipmentSlot.CHEST, Items.CHEMICAL_PROTECTION_CHEST),
        HEAD(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_HEAD),
        EYES(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_EYES),
        NOSE(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_NOSE),
        MOUTH(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_MOUTH),
        /**
         * Whether the mouth is obstructed, preventing things that require an open mouth like eating.
         */
        MOUTH_COVERED(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_MOUTH);

        public final Items defaultTag;

        private List<Predicate<LivingEntity>> tests = new ArrayList<>();

        public void registerTest(Predicate<LivingEntity> testForProtection) {
            tests.add(testForProtection);
        };

        public boolean isProtected(LivingEntity livingEntity) {
            return tests.stream().anyMatch(t -> t.test(livingEntity));
        };

        private Protection(EquipmentSlot defaultEquipmentSlot, Items defaultTag) {
            this.defaultTag = defaultTag;
            registerTest(le -> defaultTag.matches(le.getItemBySlot(defaultEquipmentSlot).getItem()));
        };

        static {
            NOSE.registerTest(le -> le.hasEffect(DestroyMobEffects.FRAGRANCE));
        };
    };

    // Give cancer if unprotected in direct sunlight
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        PollutionType<Level> ozoneDepletion = DestroyPollutionTypes.OZONE_DEPLETION.get();
        int max = PollutionHelper.getLevelPollutionTypeProperties(ozoneDepletion).max();
        if (player.level().canSeeSky(player.blockPosition()) && !player.hasEffect(DestroyMobEffects.SUN_PROTECTION) && player.getRandom().nextInt(max * 600) < PollutionHelper.getPollution(player.level(), player.blockPosition(), ozoneDepletion)) player.addEffect(DestroyMobEffects.cancerInstance());
    };

    /**
     * Prevent eating if protective headwear is worn.
     */
    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();

        var food = stack.get(DataComponents.FOOD);
        if (food != null) {
            if (DestroySubstancesConfigs.babyBlueEnabled() && stack.getItem() != DestroyItems.BABY_BLUE_POWDER.get() && player.hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL) && !food.canAlwaysEat()) {
                player.displayClientMessage(DestroyLang.translate("tooltip.eating_prevented.baby_blue").component(), true);
                event.setCanceled(true);
            };
        };
    };

    /**
     * Damage entities with the effects of chemicals if they take off contaminated armor without washing it first.
     * @param event
     */
    @SubscribeEvent
    public static final void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.MAINHAND || event.getSlot() == EquipmentSlot.OFFHAND) return;
        FluidStack contamination = getContamination(event.getFrom());
        if (contamination.isEmpty()) return;
        ChemistryHazardHelper.damage(event.getEntity().level(), event.getEntity(), contamination, true);
        ChemistryHazardHelper.decontaminate(event.getFrom());
    };
};
