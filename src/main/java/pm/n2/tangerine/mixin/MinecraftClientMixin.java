package pm.n2.tangerine.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.modules.visuals.GlowESPModule;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    public void tangerine$applyOutline(CallbackInfoReturnable<Boolean> cir) {
        if (GlowESPModule.INSTANCE.getEnabled().getValue()) {
            cir.setReturnValue(true);
        }
    }
}
