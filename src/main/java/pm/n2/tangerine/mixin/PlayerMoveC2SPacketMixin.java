package pm.n2.tangerine.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;

@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin {
	@Mutable
	@Shadow
	@Final
	protected boolean onGround;

	@Mutable
	@Shadow
	@Final
	protected double x;

	@Mutable
	@Shadow
	@Final
	protected double z;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void tangerine$init(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean changePosition, boolean changeLook, CallbackInfo ci) {
		if (Tangerine.MODULE_STATE.noFall) {
			this.onGround = true;
		}

		if (Tangerine.MODULE_STATE.loMovementFix) {
			this.x = Math.floor(x * 1000) / 1000d;
			this.z = Math.floor(z * 1000) / 1000d;
		}
	}
}
