package petrolpark.mc.destroy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import petrolpark.mc.destroy.DestroyTags.Items;

/**
 * PORT (1.21.1): 1.20.5 turned {@code ArmorMaterial} from an interface mods implemented (as an enum,
 * in Destroy's case) into a registered record: the per-slot durability multiplier is gone (durability
 * is an Item property now) and the armour texture is described by a {@link ArmorMaterial.Layer}
 * instead of the material's name.
 */
public class DestroyArmorMaterials {

    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Destroy.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HAZMAT = ARMOR_MATERIALS.register("hazmat", () -> new ArmorMaterial(
        defense(1, 1, 1, 1),
        0, // Enchantability
        SoundEvents.ARMOR_EQUIP_LEATHER,
        () -> Ingredient.of(Items.TEXTILE_PLASTICS.tag),
        List.of(new ArmorMaterial.Layer(Destroy.asResource("hazmat"))),
        0f, // Toughness
        0f  // Knockback resistance
    ));

    /** The 1.20.1 material carried these as an {@code int[]} in boots-to-helmet order. */
    private static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        return new EnumMap<>(Map.of(
            ArmorItem.Type.BOOTS, boots,
            ArmorItem.Type.LEGGINGS, leggings,
            ArmorItem.Type.CHESTPLATE, chestplate,
            ArmorItem.Type.HELMET, helmet,
            ArmorItem.Type.BODY, chestplate
        ));
    };

    public static Holder<ArmorMaterial> hazmat() {
        return HAZMAT;
    };

    public static final void register(IEventBus modEventBus) {
        ARMOR_MATERIALS.register(modEventBus);
    };

};
