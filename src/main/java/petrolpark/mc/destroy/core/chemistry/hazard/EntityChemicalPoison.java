package petrolpark.mc.destroy.core.chemistry.hazard;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import petrolpark.mc.destroy.DestroyAttachmentTypes;
import petrolpark.mc.destroy.chemistry.legacy.LegacySpecies;

/**
 * The Molecule with which a Living Entity is currently poisoned, if any.
 * <p>
 * PORT (1.21.1): this was {@code EntityChemicalPoisonCapability}, a Forge capability attached to
 * every Living Entity by an event handler, with an {@code ICapabilityProvider} and hand-rolled NBT.
 * NeoForge replaced entity capabilities with data attachments, so the provider is gone - the
 * attachment is declared in {@link DestroyAttachmentTypes} and attaches itself lazily on first
 * access. The Molecule is still stored as its full ID.
 * </p>
 */
public class EntityChemicalPoison {

    public static final Codec<EntityChemicalPoison> CODEC = Codec.STRING
        .optionalFieldOf("ToxicMolecule")
        .codec()
        .xmap(
            id -> {
                EntityChemicalPoison poison = new EntityChemicalPoison();
                poison.molecule = id.map(LegacySpecies::getMolecule).orElse(null);
                return poison;
            },
            poison -> Optional.ofNullable(poison.molecule).map(LegacySpecies::getFullID)
        );

    @Nullable
    private LegacySpecies molecule;

    public static void setMolecule(Entity entity, LegacySpecies molecule) {
        if (!(entity instanceof LivingEntity)) return;
        EntityChemicalPoison poison = entity.getData(DestroyAttachmentTypes.CHEMICAL_POISON);
        if (poison.molecule != null) return; // Don't replace existing poison
        poison.molecule = molecule;
        entity.setData(DestroyAttachmentTypes.CHEMICAL_POISON, poison);
        if (entity instanceof ServerPlayer serverPlayer) CatnipServices.NETWORK.sendToClient(serverPlayer, new ChemicalPoisonPacket(Optional.of(molecule.getFullID())));
    };

    public static void removeMolecule(Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        EntityChemicalPoison poison = entity.getData(DestroyAttachmentTypes.CHEMICAL_POISON);
        poison.molecule = null;
        entity.setData(DestroyAttachmentTypes.CHEMICAL_POISON, poison);
        if (entity instanceof ServerPlayer serverPlayer) CatnipServices.NETWORK.sendToClient(serverPlayer, new ChemicalPoisonPacket(Optional.empty()));
    };

    /** The Molecule poisoning the given Entity, or {@code null} if it is not chemically poisoned. */
    @Nullable
    public static LegacySpecies getMolecule(Entity entity) {
        if (!(entity instanceof LivingEntity)) return null;
        return entity.getData(DestroyAttachmentTypes.CHEMICAL_POISON).molecule;
    };

    @Nullable
    public LegacySpecies getMolecule() {
        return molecule;
    };

    void setMolecule(@Nullable LegacySpecies molecule) {
        this.molecule = molecule;
    };

};
