package pm.n2.tangerine.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.AntiHungerModule;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	// i tried @Inject() into this and ci.cancel() but it caused some weird rubberbanding
	// lol
	@Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
	public void tangerine$sendMovementPackets(ClientPlayNetworkHandler instance, Packet<?> packet) {
		if (Tangerine.MODULE_MANAGER.get(AntiHungerModule.class).enabled) return;
		instance.sendPacket(packet);
	}
}
