package petrolpark.mc.destroy.core.chemistry.hazard.mobeffect;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import petrolpark.mc.destroy.DestroyMobEffects;
import petrolpark.mc.destroy.DestroyPackets;

/**
 * PORT (1.21.1): was {@code CryingS2CPacket}. Crying is a Client-only visual effect, so it is still
 * applied as a Mob Effect Instance on the Client rather than synced as one.
 */
public record CryingPacket(boolean isCrying, int entityId) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, CryingPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, CryingPacket::isCrying,
        ByteBufCodecs.VAR_INT, CryingPacket::entityId,
        CryingPacket::new
    );

    public CryingPacket(LivingEntity entity, boolean isCrying) {
        this(isCrying, entity.getId());
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        return DestroyPackets.CRYING;
    };

    @Override
    public void handle(LocalPlayer player) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity entity = level.getEntity(entityId());
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (isCrying() && !livingEntity.hasEffect(DestroyMobEffects.CRYING)) {
            livingEntity.addEffect(new MobEffectInstance(DestroyMobEffects.CRYING, Integer.MAX_VALUE, 0, true, false, false));
        } else if (!isCrying()) {
            livingEntity.removeEffect(DestroyMobEffects.CRYING);
        };
    };

};
