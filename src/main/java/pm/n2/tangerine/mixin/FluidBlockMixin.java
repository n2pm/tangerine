package pm.n2.tangerine.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.LiquidWalkModule;

@ClientOnly
@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    @Final
    @Shadow
    protected FlowableFluid fluid;

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void tangerine$getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        var liquidWalk = Tangerine.INSTANCE.getModuleManager().get(LiquidWalkModule.class);
        if (liquidWalk.getEnabled().getBooleanValue()
                && (this.fluid.matchesType(Fluids.WATER)
                || this.fluid.matchesType(Fluids.FLOWING_WATER)
                || this.fluid.matchesType(Fluids.LAVA)
                || this.fluid.matchesType(Fluids.FLOWING_LAVA))) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}
