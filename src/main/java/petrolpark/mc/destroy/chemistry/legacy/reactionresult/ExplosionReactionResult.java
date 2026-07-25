package petrolpark.mc.destroy.chemistry.legacy.reactionresult;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import petrolpark.mc.destroy.chemistry.legacy.IVatReactionContext;
import petrolpark.mc.destroy.chemistry.legacy.LegacyReaction;
import petrolpark.mc.destroy.chemistry.legacy.ReactionResult;

/**
 * Blows up when enough of a Reaction takes place.
 * <p>
 * PORT (1.21.1): this used to build a {@code SmartExplosion} - Destroy's own Explosion subclass,
 * which shapes the blast irregularly, turns blocks into item stacks and carries its own damage
 * source. That class is not ported yet: it reads {@code ProtectionEnchantment}, removed in 1.21
 * when enchantments became data-driven, and depends on three other unported classes.
 * </p>
 * <p>
 * The explosion is a vanilla one for now, using the arguments {@code SmartExplosion} passed to its
 * own super-constructor: the given radius, no fire, and blocks left intact
 * ({@code BlockInteraction.KEEP}, which is {@code ExplosionInteraction.NONE} here). Reactions do
 * explode and hurt things - they just do not yet do it in Destroy's own irregular shape, and do
 * not drop the blocks they consume. {@link #irregularity} is kept on the class so it is ready
 * when {@code SmartExplosion} lands.
 * </p>
 */
public class ExplosionReactionResult extends ReactionResult {

    protected final float radius;
    protected final float irregularity;

    public static ExplosionReactionResult small(Float moles, LegacyReaction reaction) {
        return new ExplosionReactionResult(moles, reaction, 2f, 0.5f);
    };

    public ExplosionReactionResult(float moles, LegacyReaction reaction, float radius, float irregularity) {
        super(moles, reaction);
        this.radius = radius;
        this.irregularity = irregularity > 1f ? 1f : irregularity;
    };

    public float getRadius() {
        return radius;
    };

    public float getIrregularity() {
        return irregularity;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin) {
        if (level instanceof ServerLevel serverLevel) explode(serverLevel, VecHelper.getCenterOf(basin.getBlockPos()));
    };

    @Override
    public void onVatReaction(Level level, IVatReactionContext vat) {
        vat.explode(radius, irregularity);
    };

    private void explode(Level level, Vec3 position) {
        level.explode(null, null, null, position.x, position.y, position.z, radius, false, Level.ExplosionInteraction.NONE);
    };

};
