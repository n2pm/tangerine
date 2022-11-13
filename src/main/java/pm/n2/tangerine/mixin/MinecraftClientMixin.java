package pm.n2.tangerine.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.visuals.GlowESPModule;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
	public void tangerine$applyOutline(CallbackInfoReturnable<Boolean> cir) {
		if (Tangerine.MODULE_MANAGER.get(GlowESPModule.class).enabled.getBooleanValue()) {
			cir.setReturnValue(true);
		}
	}
}
