package petrolpark.mc.destroy;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import petrolpark.mc.destroy.core.explosion.SmartExplosionS2CPacket;
import petrolpark.mc.destroy.core.chemistry.hazard.ChemicalPoisonPacket;
import petrolpark.mc.destroy.core.chemistry.hazard.mobeffect.CryingPacket;
import petrolpark.mc.destroy.core.chemistry.storage.measuringcylinder.TransferFluidPacket;
import petrolpark.mc.destroy.core.pollution.ChunkPollutionPacket;
import petrolpark.mc.destroy.core.pollution.LevelPollutionPacket;

public enum DestroyPackets implements BasePacketPayload.PacketTypeProvider {

    // Server -> client
    CHEMICAL_POISON(ChemicalPoisonPacket.class, ChemicalPoisonPacket.STREAM_CODEC),
    CHUNK_POLLUTION(ChunkPollutionPacket.class, ChunkPollutionPacket.STREAM_CODEC),
    CRYING(CryingPacket.class, CryingPacket.STREAM_CODEC),
    LEVEL_POLLUTION(LevelPollutionPacket.class, LevelPollutionPacket.STREAM_CODEC),
    SMART_EXPLOSION(SmartExplosionS2CPacket.class, SmartExplosionS2CPacket.STREAM_CODEC),

    // Client -> server
    TRANSFER_FLUID(TransferFluidPacket.class, TransferFluidPacket.STREAM_CODEC)
    ;

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> DestroyPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Destroy.asResource(name().toLowerCase())),
			clazz, codec
		);
	};

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>)type.type();
    };

    public static final void register() {
		final CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Destroy.MOD_ID, 1);
		for (DestroyPackets packet : DestroyPackets.values()) {
			packetRegistry.registerPacket(packet.type);
		};
		packetRegistry.registerAllPackets();
	};
};
