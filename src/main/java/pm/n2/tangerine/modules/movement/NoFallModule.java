package pm.n2.tangerine.modules.movement;

import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.shape.VoxelShapes;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class NoFallModule extends Module {
	public NoFallModule() {
		super("no_fall", "No fall damage", "Prevents you from taking fall damage", ModuleCategory.MOVEMENT);
	}

	private boolean wasFlying = false;
	private boolean isFlying = false;
	private int ticks = 0;
	@Override
	public void onStartTick(MinecraftClient mc) {
		if (mc.player != null && mc.world != null && this.enabled.getBooleanValue()) {
			isFlying = mc.player.getAbilities().flying;
		}
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null && mc.world != null && this.enabled.getBooleanValue()) {
			if (mc.player.fallDistance > 3F) {
				var pos = mc.player.getPos();

				var blockPos = mc.player.getBlockPos();
				blockPos = blockPos.add(0, -1.5, 0);
				blockPos = blockPos.add(0, mc.player.getVelocity().getY() * 0.1, 0);

				var blockState = mc.world.getBlockState(blockPos);
				var negateWater = Tangerine.MODULE_MANAGER.get(LiquidWalkModule.class).enabled.getBooleanValue() && (blockState.getBlock() instanceof FluidBlock || blockState.getFluidState() != Fluids.EMPTY.getDefaultState());
				if (blockState.getCollisionShape(mc.world, blockPos) != VoxelShapes.empty() || negateWater || (!wasFlying && isFlying) || !isFlying) {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY() + 0.01F, pos.getZ(), false));
					mc.player.fallDistance = 0;
				}
			}

			ticks++;
			if (ticks % 2 == 0) {
				wasFlying = mc.player.getAbilities().flying;
				ticks = 0;
			}
		}
	}
}
