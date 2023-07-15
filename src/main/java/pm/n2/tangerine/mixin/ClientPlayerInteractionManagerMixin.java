package pm.n2.tangerine.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.modules.combat.CritsModule;
import pm.n2.tangerine.modules.misc.FastBreakModule;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Shadow
    private int blockBreakingCooldown;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void tangerine$crits(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (CritsModule.INSTANCE.getEnabled().getValue()) {
            var sprinting = player.isSprinting();

            if (sprinting) {
                player.setSprinting(false);
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }

            var pos = player.getPos();
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY() + 0.05, pos.getZ(), false));
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), false));

            if (sprinting) {
                player.setSprinting(true);
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "updateBlockBreakingProgress")
    private void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (FastBreakModule.INSTANCE.getEnabled().getValue()) this.blockBreakingCooldown = 0;
    }

    @ModifyConstant(
            method = "updateBlockBreakingProgress",
            constant = @Constant(
                    ordinal = 2,
                    floatValue = 1.0F
            )
    )
    private float updateBlockBreakingProgress$currentBreakingProgress(float orig) {
        if (FastBreakModule.INSTANCE.getEnabled().getValue()) {
            return 0.7F;
        } else {
            return 1.0F;
        }
    }
}
