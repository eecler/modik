package petrolpark.mc.destroy.chemistry.legacy;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import petrolpark.mc.destroy.DestroyAdvancementTrigger;

/**
 * What a Vat offers to a {@link ReactionResult} when a Reaction finishes inside it.
 * <p>
 * PORT (1.21.1): {@link ReactionResult#onVatReaction} used to take Destroy's
 * {@code VatControllerBlockEntity} directly, which made the chemistry engine depend on ~900
 * lines of content and everything that content in turn pulls in. The Reaction Results only
 * ever used three things from the Vat, so those three are declared here instead and
 * {@code VatControllerBlockEntity} implements this interface when it is ported.
 * </p>
 * <p>
 * Addons which subclass {@link ReactionResult} should implement against this rather than the
 * Vat Block Entity.
 * </p>
 * @since Destroy 0.2.0
 */
public interface IVatReactionContext {

    /**
     * Put a precipitated Item Stack into the Vat's inventory, stacking it with what is
     * already there where possible.
     */
    void insertPrecipitate(ItemStack stack);

    /**
     * The Player who placed this Vat, if it still remembers one.
     */
    @Nullable
    Player getPlacer();

    /**
     * Award a Destroy Advancement to whoever placed this Vat, if anyone.
     */
    void awardAdvancement(DestroyAdvancementTrigger advancement);

};
