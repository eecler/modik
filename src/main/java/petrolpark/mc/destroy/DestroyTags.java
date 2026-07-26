package petrolpark.mc.destroy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import petrolpark.mc.library.util.Lang;

public class DestroyTags {

    /**
     * A common (<code>c:</code> namespace) Item tag.
     * <p>
     * These were <code>forge:</code> tags before 1.21. Create's {@code AllTags.commonItemTag}
     * does the same thing but its whole tag-helper family is deprecated for removal, so
     * Destroy builds the key itself rather than depending on it.
     * </p>
     */
    public static TagKey<Item> commonItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    };

    /**
     * A common (<code>c:</code> namespace) Block tag. See {@link #commonItemTag(String)}.
     */
    public static TagKey<Block> commonBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
    };

    public enum Blocks {

        ACID_RAIN_DESTROYS,
        ACID_RAIN_SETS_DEAD_BUSH,
        ACID_RAIN_SETS_DIRT,
        GANGUE,
        ;

        public final TagKey<Block> tag;

        private Blocks() {
            tag = TagKey.create(Registries.BLOCK, Destroy.asResource(Lang.asId(name())));
        };

    };
  
    public enum Items {

        BONEMEAL_BYPASSES_POLLUTION("bonemeal/bypasses_pollution"),

        CONTAMINABLE,
        TEST_TUBE_RACK_STORABLE,

        CHEMICAL_PROTECTION_EYES("chemical_protection/eyes"),
        CHEMICAL_PROTECTION_NOSE("chemical_protection/nose"),
        CHEMICAL_PROTECTION_MOUTH("chemical_protection/mouth"),
        CHEMICAL_PROTECTION_HEAD("chemical_protection/head"),
        CHEMICAL_PROTECTION_CHEST("chemical_protection/chest"),
        CHEMICAL_PROTECTION_LEGS("chemical_protection/legs"),
        CHEMICAL_PROTECTION_FEET("chemical_protection/feet"),

        PLASTICS,
        TEXTILE_PLASTICS("plastics/textile"),
        TRANSPARENT_PLASTICS("plastics/transparent"),

        LIABLE_TO_CHANGE,
        OBLITERATION_EXPLOSIVES,
        PRIMARY_EXPLOSIVES,
        SECONDARY_EXPLOSIVES,
        ;

        public final TagKey<Item> tag;

        private Items() {
            tag = TagKey.create(Registries.ITEM, Destroy.asResource(Lang.asId(name())));
        };

        private Items(String path) {
            tag = TagKey.create(Registries.ITEM, Destroy.asResource(path));
        };

        @SuppressWarnings("deprecation") // Create does it therefore so can I
        public boolean matches(Item item) {
            return item.builtInRegistryHolder().is(tag);
        };

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        };
    };
};
