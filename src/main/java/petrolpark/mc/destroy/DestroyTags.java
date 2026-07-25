package petrolpark.mc.destroy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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

    public enum Blocks {

        ACID_RAIN_DESTROYS,
        ACID_RAIN_SETS_DEAD_BUSH,
        ACID_RAIN_SETS_DIRT,
        ;

        public final TagKey<Block> tag;

        private Blocks() {
            tag = TagKey.create(Registries.BLOCK, Destroy.asResource(Lang.asId(name())));
        };

    };
  
    public enum Items {

        BONEMEAL_BYPASSES_POLLUTION("bonemeal/bypasses_pollution"),
        ;

        public final TagKey<Item> tag;

        private Items() {
            tag = TagKey.create(Registries.ITEM, Destroy.asResource(Lang.asId(name())));
        };

        private Items(String path) {
            tag = TagKey.create(Registries.ITEM, Destroy.asResource(path));
        };
    };
};
