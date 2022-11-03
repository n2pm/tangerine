package pm.n2.tangerine.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.FlightModule;

@Mixin(Mouse.class)
public class MouseMixin {
	@Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"))
	private boolean isSpectatorOrFlyScrollSpeed(ClientPlayerEntity instance) {
		// TODO: replace false with a ConfigBoolean(Hotkeyed?) from cauldron
		var flightModule = (FlightModule) Tangerine.MODULE_MANAGER.get(FlightModule.class);
		return instance.isSpectator() || flightModule.flyScrollSpeed.getBooleanValue();
	}
}
