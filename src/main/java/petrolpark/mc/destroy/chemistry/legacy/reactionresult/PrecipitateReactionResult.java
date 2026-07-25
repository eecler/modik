package petrolpark.mc.destroy.chemistry.legacy.reactionresult;

import petrolpark.mc.destroy.chemistry.legacy.IVatReactionContext;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import petrolpark.mc.destroy.chemistry.legacy.LegacyReaction;
import petrolpark.mc.destroy.chemistry.legacy.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrecipitateReactionResult extends ReactionResult {
    
    private final Supplier<ItemStack> precipitate;

    public static BiFunction<Float, LegacyReaction, ReactionResult> of(Supplier<ItemStack> precipitate) {
        return (m, r) -> new PrecipitateReactionResult(m, r, precipitate);
    };

    public PrecipitateReactionResult(float moles, LegacyReaction reaction, Supplier<ItemStack> precipitate) {
        super(moles, reaction);
        this.precipitate = precipitate;
    };

    public ItemStack getPrecipitate() {
        return precipitate.get();
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin) {
        // Do nothing, this is handled in ReactionInBasinRecipe
    };

    @Override
    public void onVatReaction(Level level, IVatReactionContext vat) {
        vat.insertPrecipitate(precipitate.get());
    };

    @Override
    public Collection<PrecipitateReactionResult> getAllPrecipitates() {
        return Collections.singleton(this);
    };

};
