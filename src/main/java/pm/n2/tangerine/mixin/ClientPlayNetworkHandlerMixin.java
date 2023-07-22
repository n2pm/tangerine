package pm.n2.tangerine.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.modules.movement.IgnoreRotationModule;
import pm.n2.tangerine.modules.visuals.BlockESPModule;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method = "onChunkData")
    private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo ci) {
        var world = MinecraftClient.getInstance().world;
        if (world == null) return;
        var chunk = world.getChunk(packet.getX(), packet.getZ());
        if (chunk == null) return;
        BlockESPModule.INSTANCE.searchChunkSync(chunk);
    }

    @Inject(at = @At("HEAD"), method = "onUnloadChunk")
    private void onChunkUnload(UnloadChunkS2CPacket packet, CallbackInfo ci) {
        BlockESPModule.INSTANCE.unloadChunk(packet.getX() * 16, packet.getZ() * 16);
    }

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (IgnoreRotationModule.INSTANCE.getEnabled().getValue()) {
            var instance = MinecraftClient.getInstance();
            var player = instance.player;
            if (player == null) return;
            var packetAccessor = (PlayerPositionLookS2CPacketAccessor) packet;
            packetAccessor.setYaw(player.getYaw());
            packetAccessor.setPitch(player.getPitch());
        }
    }
}
