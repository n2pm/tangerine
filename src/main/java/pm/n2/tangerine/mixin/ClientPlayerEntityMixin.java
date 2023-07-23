package pm.n2.tangerine.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.managers.PaperClipManager;
import pm.n2.tangerine.modules.movement.NoSlowModule;
import pm.n2.tangerine.modules.movement.OmniSprintModule;
import pm.n2.tangerine.modules.player.AntiHungerModule;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    public boolean tangerine$clipVehicle(ClientPlayerEntity instance) {
        if (PaperClipManager.INSTANCE.isRunning()) return false;
        return instance.hasVehicle();
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    public void tangerine$sendMovementPackets$clip(CallbackInfo ci) {
        if (PaperClipManager.INSTANCE.isRunning()) ci.cancel();
    }

    // i tried @Inject() into this and ci.cancel() but it caused some weird rubberbanding (lol)
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    public void tangerine$sendMovementPackets$antiHunger(ClientPlayNetworkHandler instance, Packet<?> packet) {
        if (AntiHungerModule.INSTANCE.getEnabled().getValue()) return;
        instance.sendPacket(packet);
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    public void tangerine$noSlow(CallbackInfoReturnable<Boolean> cir) {
        var noSlowMod = NoSlowModule.INSTANCE;
        if (noSlowMod.getEnabled().getValue() && noSlowMod.getAffectSneaking().getValue()) {
            cir.setReturnValue(((ClientPlayerEntity) (Object) this).isCrawling());
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean tangerine$noSlow_items(ClientPlayerEntity instance) {
        if (NoSlowModule.INSTANCE.getEnabled().getValue()) return false;
        return instance.isUsingItem();
    }

    @Inject(method = "isWalking", at = @At("HEAD"), cancellable = true)
    public void tangerine$omniSprint(CallbackInfoReturnable<Boolean> cir) {
        var self = (ClientPlayerEntity) (Object) this;
        if (OmniSprintModule.INSTANCE.getEnabled().getValue()) {
            cir.setReturnValue(self.isSubmergedInWater()
                    ? self.input.hasForwardMovement()
                    : (self.input.movementForward >= 0.8
                    || self.input.movementForward <= -0.8
                    || self.input.movementSideways >= 0.8
                    || self.input.movementSideways <= -0.8));
        }
    }
}
