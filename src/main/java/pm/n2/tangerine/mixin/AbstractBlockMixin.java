package pm.n2.tangerine.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.movement.LiquidWalkModule;

@Environment(EnvType.CLIENT)
@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
	@Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
	public void tangerine$jesus_waterlogged(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (Tangerine.MODULE_MANAGER.get(LiquidWalkModule.class).enabled.getBooleanValue()
				&& state.getFluidState() != Fluids.EMPTY.getDefaultState()
				&& state.offsetType() == AbstractBlock.OffsetType.NONE
		) {
			cir.setReturnValue(VoxelShapes.fullCube());
		}
	}
}
