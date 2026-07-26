package petrolpark.mc.destroy;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import petrolpark.mc.destroy.config.DestroyConfigs;
import petrolpark.mc.destroy.core.chemistry.hazard.protection.ChemistryProtectionHeadwearItem;
import petrolpark.mc.destroy.core.chemistry.hazard.protection.HazmatSuitArmorItem;

/**
 * PORT (1.21.1): a first slice of Destroy's items - the ones the Reaction index names, so that
 * Reactions can run. The 1.20.1 class is 808 lines and pulls in some forty content classes
 * (syringes, circuit boards, hazmat gear, the Swiss Army Knife...); those items come back with
 * their content.
 * <p>
 * Five of these are registered as plain Items for now, because their behaviour lives in classes
 * that are not ported yet. They exist, stack and take part in Reactions, but do nothing special:
 * </p>
 * <ul>
 * <li>{@link #SODIUM_INGOT} - should rust into {@link #OXIDIZED_SODIUM_INGOT} over time ({@code OxidizingItem})</li>
 * <li>{@link #SODIUM_HYDRIDE} - should ignite on contact with water ({@code WaterSensitiveSpontaneouslyCombustingItem})</li>
 * <li>{@link #TOUCH_POWDER} - should explode when touched ({@code ContactExplosiveItem})</li>
 * <li>{@link #IODINE} - should release Dragon's Breath when burnt ({@code IodineItem})</li>
 * <li>{@link #CREATINE} - should be edible and give its effects ({@code CreatineItem} + {@code DestroyFoods})</li>
 * </ul>
 */
public class DestroyItems {

    // Polymers and plastics
    public static final ItemEntry<Item>

    ABS = REGISTRATE.item("abs", Item::new).register(),
    NYLON = REGISTRATE.item("nylon", Item::new).register(),
    POLYACRYLONITRILE = REGISTRATE.item("polyacrylonitrile", Item::new).register(),
    POLYETHENE = REGISTRATE.item("polyethene", Item::new).register(),
    POLYISOPRENE = REGISTRATE.item("polyisoprene", Item::new).register(),
    POLYMETHYL_METHACRYLATE = REGISTRATE.item("polymethyl_methacrylate", Item::new).register(),
    POLYPROPENE = REGISTRATE.item("polypropene", Item::new).register(),
    POLYSTYRENE = REGISTRATE.item("polystyrene", Item::new).register(),
    POLYSTYRENE_BUTADIENE = REGISTRATE.item("polystyrene_butadiene", Item::new).register(),
    POLYTETRAFLUOROETHENE = REGISTRATE.item("polytetrafluoroethene", Item::new).register(),
    POLYURETHANE = REGISTRATE.item("polyurethane", Item::new).register(),
    POLYVINYL_CHLORIDE = REGISTRATE.item("polyvinyl_chloride", Item::new).register(),

    // Minerals and powders
    BORAX = REGISTRATE.item("borax", Item::new).register(),
    CALCIUM_CARBIDE = REGISTRATE.item("calcium_carbide", Item::new).register(),
    CHALK_DUST = REGISTRATE.item("chalk_dust", Item::new).register(),
    CRUSHED_RAW_CHROMIUM = REGISTRATE.item("crushed_raw_chromium", Item::new).register(),
    FLUORITE = REGISTRATE.item("fluorite", Item::new).register(),
    NETHER_CROCOITE = REGISTRATE.item("nether_crocoite", Item::new).register(),
    SILICA = REGISTRATE.item("silica", Item::new).register(),
    ZEOLITE = REGISTRATE.item("zeolite", Item::new).register(),

    // Explosives
    ACETONE_PEROXIDE = REGISTRATE.item("acetone_peroxide", Item::new).register(),
    ANFO = REGISTRATE.item("anfo", Item::new).tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag).register(),
    CORDITE = REGISTRATE.item("cordite_rods", Item::new).tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag).register(),
    DYNAMITE = REGISTRATE.item("dynamite", Item::new).tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag).register(),
    FULMINATED_MERCURY = REGISTRATE.item("fulminated_mercury", Item::new).register(),
    NANODIAMONDS = REGISTRATE.item("nanodiamonds", Item::new).register(),
    NICKEL_HYDRAZINE_NITRATE = REGISTRATE.item("nickel_hydrazine_nitrate", Item::new).register(),
    NITROCELLULOSE = REGISTRATE.item("nitrocellulose", Item::new).register(),
    PICRIC_ACID_TABLET = REGISTRATE.item("picric_acid_tablet", Item::new).tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag).register(),
    TOUCH_POWDER = REGISTRATE.item("touch_powder", Item::new).register(), // PORT: ContactExplosiveItem

    // Sodium
    OXIDIZED_SODIUM_INGOT = REGISTRATE.item("oxidized_sodium_ingot", Item::new).register(),
    SODIUM_INGOT = REGISTRATE.item("sodium_ingot", Item::new).register(), // PORT: OxidizingItem
    SODIUM_HYDRIDE = REGISTRATE.item("sodium_hydride", Item::new).register(), // PORT: WaterSensitiveSpontaneouslyCombustingItem

    // Miscellaneous products
    BABY_BLUE_CRYSTAL = REGISTRATE.item("baby_blue_crystal", Item::new).register(),
    CARD_STOCK = REGISTRATE.item("card_stock", Item::new).register(),
    MESH = REGISTRATE.item("mesh", Item::new).register(),
    CREATINE = REGISTRATE.item("creatine", Item::new).register(), // PORT: CreatineItem + DestroyFoods
    IODINE = REGISTRATE.item("iodine", Item::new).register(), // PORT: IodineItem
    BABY_BLUE_POWDER = REGISTRATE.item("baby_blue_powder", Item::new).register(), // PORT: DestroyFoods
    GAS_FILTER = REGISTRATE.item("gas_filter", Item::new).register();

    // Personal protective equipment
    public static final ItemEntry<ChemistryProtectionHeadwearItem>

    LABORATORY_GOGGLES = REGISTRATE.item("laboratory_goggles", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p.stacksTo(1))
        .model(NonNullBiConsumer.noop()) // Hand-copied: these have a custom head-worn model
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_EYES.tag)
        .onRegister(ChemistryProtectionHeadwearItem.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyConfigs.server().equipment.laboratoryGogglesDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(DestroyTags.Items.TRANSPARENT_PLASTICS.tag)))
        .register(),

    GOLD_LABORATORY_GOGGLES = REGISTRATE.item("gold_laboratory_goggles", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p.stacksTo(1))
        .model(NonNullBiConsumer.noop()) // Hand-copied: these have a custom head-worn model
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_EYES.tag)
        .onRegister(ChemistryProtectionHeadwearItem.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyConfigs.server().equipment.goldLaboratoryGogglesDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(DestroyTags.commonItemTag("plates/gold"))))
        .onRegister(ChemistryProtectionHeadwearItem.enchantable())
        .register(),

    PAPER_MASK = REGISTRATE.item("paper_mask", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p.stacksTo(1))
        .model(NonNullBiConsumer.noop()) // Hand-copied: these have a custom head-worn model
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_NOSE.tag, DestroyTags.Items.CHEMICAL_PROTECTION_MOUTH.tag)
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyConfigs.server().equipment.paperMaskDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(net.minecraft.world.item.Items.PAPER)))
        .register(),

    GAS_MASK = REGISTRATE.item("gas_mask", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p.stacksTo(1))
        .model(NonNullBiConsumer.noop()) // Hand-copied: these have a custom head-worn model
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_HEAD.tag, DestroyTags.Items.CHEMICAL_PROTECTION_EYES.tag, DestroyTags.Items.CHEMICAL_PROTECTION_NOSE.tag, DestroyTags.Items.CHEMICAL_PROTECTION_MOUTH.tag, DestroyTags.Items.CONTAMINABLE.tag)
        .onRegister(ChemistryProtectionHeadwearItem.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyConfigs.server().equipment.gasMaskDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(DestroyTags.Items.TEXTILE_PLASTICS.tag)))
        .register();

    public static final ItemEntry<? extends HazmatSuitArmorItem>

    HAZMAT_SUIT = REGISTRATE.item("hazmat_suit", p -> new HazmatSuitArmorItem(ArmorItem.Type.CHESTPLATE, p))
        .properties(p -> p.stacksTo(1))
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_CHEST.tag, DestroyTags.Items.CONTAMINABLE.tag)
        .register(),
    HAZMAT_LEGGINGS = REGISTRATE.item("hazmat_leggings", p -> new HazmatSuitArmorItem(ArmorItem.Type.LEGGINGS, p))
        .properties(p -> p.stacksTo(1))
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_LEGS.tag, DestroyTags.Items.CONTAMINABLE.tag)
        .register(),
    WELLINGTON_BOOTS = REGISTRATE.item("wellington_boots", p -> new HazmatSuitArmorItem(ArmorItem.Type.BOOTS, p))
        .properties(p -> p.stacksTo(1))
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_FEET.tag, DestroyTags.Items.CONTAMINABLE.tag)
        .register();

    public static final void register() {};

};
