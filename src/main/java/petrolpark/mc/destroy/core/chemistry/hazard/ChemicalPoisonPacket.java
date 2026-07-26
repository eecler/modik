package petrolpark.mc.destroy.core.chemistry.hazard;

import java.util.Optional;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.destroy.DestroyPackets;
import petrolpark.mc.destroy.chemistry.legacy.LegacySpecies;

/**
 * Tells the Client which Molecule is poisoning it, so the Chemical Poison tooltip can name it.
 * <p>
 * PORT (1.21.1): was {@code ChemicalPoisonS2CPacket}, a Forge {@code S2CPacket} with
 * {@code toBytes}/{@code handle(Supplier<Context>)}. Payloads are records with a Stream Codec now,
 * so the "NO_MOLECULE" sentinel the old buffer used is just an empty Optional.
 * </p>
 */
public record ChemicalPoisonPacket(Optional<String> moleculeId) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalPoisonPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), ChemicalPoisonPacket::moleculeId,
        ChemicalPoisonPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return DestroyPackets.CHEMICAL_POISON;
    };

    @Override
    public void handle(LocalPlayer player) {
        if (moleculeId().isEmpty()) {
            EntityChemicalPoison.removeMolecule(player);
            return;
        };
        LegacySpecies molecule = LegacySpecies.getMolecule(moleculeId().get());
        if (molecule != null) EntityChemicalPoison.setMolecule(player, molecule);
    };

};
