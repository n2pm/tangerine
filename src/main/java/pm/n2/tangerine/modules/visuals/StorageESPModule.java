package pm.n2.tangerine.modules.visuals;

import com.adryd.cauldron.api.render.helper.OverlayRenderManager;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.mixin.ClientChunkManagerAccessor;
import pm.n2.tangerine.mixin.ClientChunkMapAccessor;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;
import pm.n2.tangerine.render.OverlayStorageESP;

import java.util.Arrays;
import java.util.HashMap;

public class StorageESPModule extends Module {
	public HashMap<BlockPos, BlockEntity> blockEntities = new HashMap<>();

	public StorageESPModule() {
		super("Storage ESP", "Highlights storage blocks", ModuleCategory.VISUALS);
		OverlayRenderManager.addRenderer(new OverlayStorageESP());
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		var world = mc.world;
		if (mc.world == null || !this.enabled) return;

		var chunkManager = world.getChunkManager();

		var chunkMap = ((ClientChunkManagerAccessor) chunkManager).getChunks();
		var chunks = ((ClientChunkMapAccessor) (Object) chunkMap).getChunks();

		var allowedEntities = new Class[]{
				ChestBlockEntity.class,
				TrappedChestBlockEntity.class,
				BarrelBlockEntity.class,
				EnderChestBlockEntity.class,
				ShulkerBoxBlockEntity.class,
				FurnaceBlockEntity.class,
				BlastFurnaceBlockEntity.class,
				SmokerBlockEntity.class,
				DispenserBlockEntity.class,
				DropperBlockEntity.class,
				HopperBlockEntity.class
		};

		var newBlockEntities = new HashMap<BlockPos, BlockEntity>();
		for (var i = 0; i < chunks.length(); i++) {
			var chunk = chunks.get(i);
			if (chunk != null) {
				var blockEntities = chunk.getBlockEntities();
				for (var be : blockEntities.values()) {
					var clazz = be.getClass();

					if (Arrays.stream(allowedEntities).anyMatch(clazz::isAssignableFrom)) {
						newBlockEntities.put(be.getPos(), be);
					}
				}
			}
		}

		this.blockEntities = newBlockEntities;
	}
}
