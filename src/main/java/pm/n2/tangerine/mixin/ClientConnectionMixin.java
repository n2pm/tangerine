package pm.n2.tangerine.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.core.TangerineEvent;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        Tangerine.INSTANCE.getEventManager().dispatch(new TangerineEvent.S2CPacket(packet));
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo ci) {
        Tangerine.INSTANCE.getEventManager().dispatch(new TangerineEvent.C2SPacket(packet));
    }
}
