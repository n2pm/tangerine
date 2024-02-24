package pm.n2.tangerine.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.modules.combat.CritsModule;
import pm.n2.tangerine.modules.misc.CivBreakModule;
import pm.n2.tangerine.modules.misc.FastBreakModule;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int blockBreakingCooldown;

    @Shadow
    public abstract boolean breakBlock(BlockPos pos);

    @Unique
    @Nullable
    BlockPos lastBlockBreak;

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

    @Inject(
        method = "attackBlock",
        at =
        @At(
            value = "INVOKE",
            target =
                "Lnet/minecraft/client/tutorial/TutorialManager;onBlockBreaking(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V",
            ordinal = 1),
        cancellable = true)
    private void setBlockBreakSpeed(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        assert this.client.world != null;
        assert this.client.player != null;
        if (civBreak(pos, direction, cir)) {
            return;
        }
        if (FastBreakModule.INSTANCE.getEnabled().getValue()) {
            BlockState blockState = this.client.world.getBlockState(pos);
            if (blockState.calcBlockBreakingDelta(this.client.player, this.client.player.getWorld(), pos) >= 0.7F) {
                this.breakBlock(pos);
                try (PendingUpdateManager pendingUpdateManager = ((ClientWorldAccessor) this.client.world)
                    .invokeGetPendingUpdateManager()
                    .incrementSequence()) {
                    this.networkHandler.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                        pos,
                        direction,
                        pendingUpdateManager.getSequence()));
                    pendingUpdateManager.incrementSequence();
                    this.networkHandler.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                        pos,
                        direction,
                        pendingUpdateManager.getSequence()));
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private boolean civBreak(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        assert this.client.world != null;
        if (CivBreakModule.INSTANCE.getEnabled().getValue() && this.lastBlockBreak != null && this.lastBlockBreak.equals(pos)) {
            try (PendingUpdateManager pendingUpdateManager = ((ClientWorldAccessor) this.client.world)
                .invokeGetPendingUpdateManager()
                .incrementSequence()) {
                this.networkHandler.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                    pos,
                    direction,
                    pendingUpdateManager.getSequence()));
            }
            this.breakBlock(pos);
            cir.setReturnValue(true);
            return true;
        } else {
            this.lastBlockBreak = null;
        }
        return false;
    }
    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void updateLastBrokenBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        this.lastBlockBreak = pos;
    }
}
