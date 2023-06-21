package pm.n2.tangerine.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.FlightModule;
import pm.n2.tangerine.modules.movement.NoFallModule;

@ClientOnly
@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin {
    @Shadow
    @Final
    @Mutable
    protected boolean onGround;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void tangerine$constructor(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean changePosition, boolean changeLook, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        var flightEnabled = Tangerine.INSTANCE.getModuleManager().get(FlightModule.class).getEnabled().getBooleanValue();
        var noFallEnabled = Tangerine.INSTANCE.getModuleManager().get(NoFallModule.class).getEnabled().getBooleanValue();
        if (player != null && player.getAbilities().flying && flightEnabled && noFallEnabled) {
            this.onGround = true;
        }
    }
}
