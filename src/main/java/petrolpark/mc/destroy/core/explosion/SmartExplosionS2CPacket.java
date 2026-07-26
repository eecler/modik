package petrolpark.mc.destroy.core.explosion;

import java.util.Optional;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.destroy.DestroyPackets;

public record SmartExplosionS2CPacket(SmartExplosion explosion, Vec3 recipientKnockback) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SmartExplosionS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buffer, packet) -> {
            buffer.writeResourceLocation(packet.explosion().getSerializer().id);
            packet.explosion().write(buffer);
            buffer.writeDouble(packet.recipientKnockback().x());
            buffer.writeDouble(packet.recipientKnockback().y());
            buffer.writeDouble(packet.recipientKnockback().z());
        },
        buffer -> {
            SmartExplosion.Serializer<?> serializer = SmartExplosion.getType(buffer.readResourceLocation());
            SmartExplosion explosion = serializer.read(buffer);
            return new SmartExplosionS2CPacket(explosion, new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
        }
    );

    public static void send(ServerPlayer player, SmartExplosion explosion) {
        Vec3 knockback = Optional.ofNullable(explosion.getHitPlayers().get(player)).orElse(Vec3.ZERO);
        CatnipServices.NETWORK.sendToClient(player, new SmartExplosionS2CPacket(explosion, knockback));
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        return DestroyPackets.SMART_EXPLOSION;
    };

    @Override
    public void handle(LocalPlayer player) {
        explosion.finalizeExplosion(true);
        player.setDeltaMovement(recipientKnockback);
    };
};
