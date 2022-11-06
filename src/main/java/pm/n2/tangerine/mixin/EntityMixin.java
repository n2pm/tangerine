package pm.n2.tangerine.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.NoSlowModule;

@Mixin(Entity.class)
public class EntityMixin {
	@Shadow
	protected Vec3d movementMultiplier;

	@Inject(method = "slowMovement", at = @At("TAIL"))
	public void tangerine$noSlow(BlockState state, Vec3d multiplier, CallbackInfo ci) {
		var self = (Entity) (Object) this;
		if (self instanceof PlayerEntity && Tangerine.MODULE_MANAGER.get(NoSlowModule.class).enabled.getBooleanValue()) {
			this.movementMultiplier = Vec3d.ZERO;
		}
	}
}
