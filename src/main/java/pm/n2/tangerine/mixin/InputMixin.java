package pm.n2.tangerine.mixin;

import net.minecraft.client.input.Input;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.modules.movement.OmniSprintModule;

@ClientOnly
@Mixin(Input.class)
public class InputMixin {
    @Inject(method = "hasForwardMovement", at = @At("HEAD"), cancellable = true)
    public void tangerine$omniSprint(CallbackInfoReturnable<Boolean> cir) {
        var self = (Input) (Object) this;
        if (OmniSprintModule.INSTANCE.getEnabled().getValue()) {
            cir.setReturnValue(self.forwardMovement > 1.0E-5F
                    || self.forwardMovement < -1.0E-5F
                    || self.sidewaysMovement > 1.0E-5F
                    || self.sidewaysMovement < -1.0E-5F);
        }
    }
}
