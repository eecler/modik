package petrolpark.mc.destroy.chemistry.legacy;

import java.util.Map;

/**
 * The outcome of {@link LegacyMixture#reactInBasin reacting a Mixture in a Basin}.
 * <p>
 * This lived in Destroy's Create-basin recipe class before the 1.21.1 port. It holds only
 * chemistry data and is produced by the chemistry engine, so it belongs here - this way the
 * engine does not have to depend on content, and the Basin recipe imports it instead.
 * </p>
 * @param ticks How many ticks of reaction occurred; {@code 0} means equilibrium was undisturbed
 * @param reactionResults The {@link ReactionResult}s which completed, and how many times each did
 * @param amount The resultant volume of Mixture
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public record ReactionInBasinResult(int ticks, Map<ReactionResult, Integer> reactionResults, int amount) {};
