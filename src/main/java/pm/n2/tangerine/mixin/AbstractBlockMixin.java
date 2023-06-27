package pm.n2.tangerine.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.modules.movement.LiquidWalkModule;

@ClientOnly
@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void tangerine$jesus_waterlogged(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        var liquidWalk = LiquidWalkModule.INSTANCE;
        if (liquidWalk.getEnabled().getValue()
                && state.getFluidState() != Fluids.EMPTY.getDefaultState()) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}
