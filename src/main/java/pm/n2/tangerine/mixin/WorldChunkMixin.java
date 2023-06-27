package pm.n2.tangerine.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.modules.visuals.BlockESPModule;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    // stolen from meteor dont tell anyone
    @Inject(at = @At("TAIL"), method = "setBlockState")
    private void setBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        BlockESPModule.INSTANCE.handleUpdate(pos, state);
    }
}
