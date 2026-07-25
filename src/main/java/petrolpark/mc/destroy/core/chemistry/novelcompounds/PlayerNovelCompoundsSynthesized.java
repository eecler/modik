package petrolpark.mc.destroy.core.chemistry.novelcompounds;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;

import net.minecraft.world.entity.player.Player;

import petrolpark.mc.destroy.DestroyAttachmentTypes;
import petrolpark.mc.destroy.DestroyStats;
import petrolpark.mc.destroy.chemistry.legacy.LegacySpecies;

/**
 * The set of novel Molecules a Player has synthesized, recorded as
 * <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> codes.
 * <p>
 * PORT (1.21.1): this was {@code PlayerNovelCompoundsSynthesizedCapability}, a Forge
 * capability with an {@code ICapabilityProvider}/{@code LazyOptional} provider and manual NBT
 * serialization. NeoForge replaced capabilities on entities with data attachments, so the
 * provider and the {@code copyFrom} plumbing are gone: the attachment is registered in
 * {@link DestroyAttachmentTypes} and copies itself across death and dimension changes.
 * </p>
 */
public class PlayerNovelCompoundsSynthesized {

    public static final Codec<PlayerNovelCompoundsSynthesized> CODEC = Codec.STRING
        .listOf()
        .xmap(
            list -> {
                PlayerNovelCompoundsSynthesized pncs = new PlayerNovelCompoundsSynthesized();
                pncs.novelCompoundFROWNSStrings.addAll(list);
                return pncs;
            },
            pncs -> List.copyOf(pncs.novelCompoundFROWNSStrings)
        );

    protected Set<String> novelCompoundFROWNSStrings = new HashSet<>();

    public static void add(Player player, LegacySpecies novelCompound) {
        PlayerNovelCompoundsSynthesized pncs = player.getData(DestroyAttachmentTypes.NOVEL_COMPOUNDS_SYNTHESIZED);
        if (pncs.novelCompoundFROWNSStrings.add(novelCompound.getFROWNSCode())) {
            player.awardStat(DestroyStats.NOVEL_COMPOUNDS_SYNTHESIZED.get());
        };
    };

    public Set<String> getAll() {
        return Set.copyOf(novelCompoundFROWNSStrings);
    };

    public void copyFrom(PlayerNovelCompoundsSynthesized pncs) {
        novelCompoundFROWNSStrings.clear();
        novelCompoundFROWNSStrings.addAll(pncs.novelCompoundFROWNSStrings);
    };

};
