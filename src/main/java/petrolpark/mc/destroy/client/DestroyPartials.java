package petrolpark.mc.destroy.client;

import java.util.ArrayList;
import java.util.List;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.lang.Lang;

import petrolpark.mc.destroy.Destroy;
import petrolpark.mc.destroy.chemistry.legacy.LegacyBond.BondType;
import petrolpark.mc.destroy.chemistry.legacy.LegacyElement;

/**
 * Partial models used by Destroy.
 * <p>
 * Only the chemistry partials (atoms, bonds and R-groups) are present so far - the block
 * partials for Vats, the Pumpjack, the Mechanical Sieve and so on come back as that content
 * is ported to 1.21.1. The {@link #block} helper is kept ready for them.
 * </p>
 */
public class DestroyPartials {

    // Atoms
    static {
        for (LegacyElement element : LegacyElement.values()) {
            if (element != LegacyElement.R_GROUP) element.setPartial(atom(Lang.asId(element.name())));
        };
    };

    // Bonds
    static {
        for (BondType bondType : BondType.values()) {
            bondType.setPartial(bond(Lang.asId(bondType.name())));
        };
    };

    // R-Groups
    public static final PartialModel R_GROUP = rGroup("generic");
    public static final List<PartialModel> rGroups = new ArrayList<>(10);
    static {
        rGroups.add(R_GROUP);
        for (int i = 1; i < 10; i++) {
            rGroups.add(rGroup(String.valueOf(i)));
        };
    };

    // Mechanical Sieve
    public static final PartialModel
    MECHANICAL_SIEVE_SHAFT = block("mechanical_sieve/shaft"),
    MECHANICAL_SIEVE_LINKAGES = block("mechanical_sieve/linkages"),
    MECHANICAL_SIEVE = block("mechanical_sieve/sieve");

    // Chemistry protection equipment
    public static final PartialModel
    LABORATORY_GOGGLES = block("laboratory_goggles"),
    GOLD_LABORATORY_GOGGLES = block("gold_laboratory_goggles"),
    GAS_MASK = block("gas_mask"),
    PAPER_MASK = block("paper_mask");

    // Tree Tap
    public static final PartialModel
    TREE_TAP_ARM = block("tree_tap/arm");

    // Custom Explosive Mix
    public static final PartialModel
    CUSTOM_EXPLOSIVE_MIX_BASE = block("custom_explosive_mix_no_overlay"),
    CUSTOM_EXPLOSIVE_MIX_OVERLAY = block("custom_explosive_mix_overlay");

    private static PartialModel block(String path) { //copied from Create source code
        return PartialModel.of(Destroy.asResource("block/"+path));
    };

    private static PartialModel atom(String path) {
        return PartialModel.of(Destroy.asResource("chemistry/atom/"+path));
    };

    private static PartialModel bond(String path) {
        return PartialModel.of(Destroy.asResource("chemistry/bond/"+path));
    };

    private static PartialModel rGroup(String path) {
        return PartialModel.of(Destroy.asResource("chemistry/r_group/"+path));
    };

    public static void init() {};

};
