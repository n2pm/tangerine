package pm.n2.tangerine.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.NoSlowModule;
import pm.n2.tangerine.modules.movement.OmniSprintModule;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "setMovementSpeed", at = @At("HEAD"), cancellable = true)
	public void tangerine$noSlow(float movementSpeed, CallbackInfo ci) {
		var self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity) {
			var defaultSpeed = ((PlayerEntity) self).getAbilities().getWalkSpeed();
			if (Tangerine.MODULE_MANAGER.get(NoSlowModule.class).enabled.getBooleanValue() && movementSpeed < defaultSpeed) {
				ci.cancel();
			}
		}
	}

	@Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
	public void tangerine$omniSprint_fixJump(LivingEntity instance, Vec3d orig) {
		if (Tangerine.MODULE_MANAGER.get(OmniSprintModule.class).enabled.getBooleanValue()) {
			var f = instance.getYaw() * (float) (Math.PI / 180.0);
			var vel = instance.getVelocity();

			var forward = new Vec3d(-MathHelper.sin(f), 0, MathHelper.cos(f));
			var side = new Vec3d(MathHelper.cos(f), 0, -MathHelper.sin(f));

			var forwardSpeed = vel.dotProduct(forward);
			var sideSpeed = vel.dotProduct(side);

			var dirForward = MathHelper.sign(Math.abs(forwardSpeed) > 0.1 ? forwardSpeed : 0);
			var dirSide = MathHelper.sign(Math.abs(sideSpeed) > 0.1 ? sideSpeed : 0);

			Vec3d forwardBoost = forward.multiply(0.2).multiply(dirForward);
			Vec3d sideBoost = side.multiply(0.2).multiply(dirSide);

			instance.setVelocity(vel.add(forwardBoost).add(sideBoost));
		} else {
			instance.setVelocity(orig);
		}
	}
}
