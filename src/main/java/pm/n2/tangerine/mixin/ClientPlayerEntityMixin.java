package pm.n2.tangerine.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.NoSlowModule;
import pm.n2.tangerine.modules.movement.OmniSprintModule;
import pm.n2.tangerine.modules.player.AntiHungerModule;

@ClientOnly
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    // i tried @Inject() into this and ci.cancel() but it caused some weird rubberbanding
    // lol
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    public void tangerine$sendMovementPackets(ClientPlayNetworkHandler instance, Packet<?> packet) {
        if (Tangerine.INSTANCE.getModuleManager().get(AntiHungerModule.class).getEnabled().getBooleanValue()) return;
        instance.sendPacket(packet);
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    public void tangerine$noSlow(CallbackInfoReturnable<Boolean> cir) {
        var noSlowMod = (NoSlowModule) Tangerine.INSTANCE.getModuleManager().get(NoSlowModule.class);
        if (noSlowMod.getEnabled().getBooleanValue() && noSlowMod.getAffectSneaking().getBooleanValue()) {
            cir.setReturnValue(((ClientPlayerEntity) (Object) this).shouldLeaveSwimmingPose());
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean tangerine$noSlow_items(ClientPlayerEntity instance) {
        if (Tangerine.INSTANCE.getModuleManager().get(NoSlowModule.class).getEnabled().getBooleanValue()) {
            return false;
        }
        return instance.isUsingItem();
    }

    @Inject(method = "isWalking", at = @At("HEAD"), cancellable = true)
    public void tangerine$omniSprint(CallbackInfoReturnable<Boolean> cir) {
        var self = (ClientPlayerEntity) (Object) this;
        if (Tangerine.INSTANCE.getModuleManager().get(OmniSprintModule.class).getEnabled().getBooleanValue()) {
            cir.setReturnValue(self.isSubmergedInWater()
                    ? self.input.hasForwardMovement()
                    : (self.input.forwardMovement >= 0.8
                    || self.input.forwardMovement <= -0.8
                    || self.input.sidewaysMovement >= 0.8
                    || self.input.sidewaysMovement <= -0.8));
        }
    }
}
