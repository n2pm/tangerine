package pm.n2.tangerine.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.NoSlowModule;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
	@ModifyArg(method = "getSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"), index = 2)
	public float tangerine$noSlow(float delta) {
		if (delta < 1.0F && Tangerine.MODULE_MANAGER.get(NoSlowModule.class).enabled.getBooleanValue()) {
			delta = 1.0F;
		}
		return delta;
	}
}
