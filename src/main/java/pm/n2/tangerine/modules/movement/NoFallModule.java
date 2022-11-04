package pm.n2.tangerine.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.ModuleCategory;
import pm.n2.tangerine.modules.Module;

public class NoFallModule extends Module {
	public NoFallModule() {
		super("No fall damage", "Prevents you from taking fall damage", ModuleCategory.MOVEMENT);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null && mc.world != null && this.enabled) {
			if (mc.player.fallDistance > 3F) {
				var pos = mc.player.getPos();

				var blockPos = mc.player.getBlockPos();
				blockPos = blockPos.add(0, -1.5, 0);
				blockPos = blockPos.add(0, mc.player.getVelocity().getY() * 0.1, 0);

				var blockState = mc.world.getBlockState(blockPos);
				if (blockState.getCollisionShape(mc.world, blockPos) != VoxelShapes.empty()) {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY() + 0.01F, pos.getZ(), false));
					mc.player.fallDistance = 0;
				}
			}
		}
	}
}
