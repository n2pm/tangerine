package pm.n2.tangerine.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.NoSlowModule;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "setMovementSpeed", at = @At("HEAD"), cancellable = true)
	public void tangerine$noSlow(float movementSpeed, CallbackInfo ci) {
		var self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity) {
			var defaultSpeed = ((PlayerEntity) self).getAbilities().getWalkSpeed();
			if (Tangerine.MODULE_MANAGER.get(NoSlowModule.class).enabled && movementSpeed < defaultSpeed) {
				ci.cancel();
			}
		}
	}
}
