package petrolpark.mc.destroy;

import static petrolpark.mc.destroy.Destroy.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Item;

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
    FULMINATED_MERCURY = REGISTRATE.item("fulminated_mercury", Item::new).register(),
    NICKEL_HYDRAZINE_NITRATE = REGISTRATE.item("nickel_hydrazine_nitrate", Item::new).register(),
    NITROCELLULOSE = REGISTRATE.item("nitrocellulose", Item::new).register(),
    TOUCH_POWDER = REGISTRATE.item("touch_powder", Item::new).register(), // PORT: ContactExplosiveItem

    // Sodium
    OXIDIZED_SODIUM_INGOT = REGISTRATE.item("oxidized_sodium_ingot", Item::new).register(),
    SODIUM_INGOT = REGISTRATE.item("sodium_ingot", Item::new).register(), // PORT: OxidizingItem
    SODIUM_HYDRIDE = REGISTRATE.item("sodium_hydride", Item::new).register(), // PORT: WaterSensitiveSpontaneouslyCombustingItem

    // Miscellaneous products
    BABY_BLUE_CRYSTAL = REGISTRATE.item("baby_blue_crystal", Item::new).register(),
    CREATINE = REGISTRATE.item("creatine", Item::new).register(), // PORT: CreatineItem + DestroyFoods
    IODINE = REGISTRATE.item("iodine", Item::new).register(); // PORT: IodineItem

    public static final void register() {};

};
