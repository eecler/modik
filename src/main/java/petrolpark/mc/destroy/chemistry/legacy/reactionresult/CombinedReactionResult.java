package petrolpark.mc.destroy.chemistry.legacy.reactionresult;

import petrolpark.mc.destroy.chemistry.legacy.IVatReactionContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import petrolpark.mc.destroy.chemistry.legacy.LegacyReaction;
import petrolpark.mc.destroy.chemistry.legacy.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.Level;

public class CombinedReactionResult extends ReactionResult {

    protected List<ReactionResult> childResults;

    public CombinedReactionResult(float moles, LegacyReaction reaction) {
        super(moles, reaction);
        childResults = new ArrayList<>();
    };

    public CombinedReactionResult with(BiFunction<Float, LegacyReaction, ReactionResult> result) {
        childResults.add(result.apply(moles, reaction));
        return this;
    };

    public List<ReactionResult> getChildren() {
        return childResults;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin) {
        for (ReactionResult childResult : childResults) {
            childResult.onBasinReaction(level, basin);
        };
    };

    @Override
    public void onVatReaction(Level level, IVatReactionContext vat) {
        for (ReactionResult childResult : childResults) {
            childResult.onVatReaction(level, vat);
        };
    };

    @Override
    public Collection<PrecipitateReactionResult> getAllPrecipitates() {
        return childResults.stream().flatMap(r -> r.getAllPrecipitates().stream()).toList();
    };
    
};
