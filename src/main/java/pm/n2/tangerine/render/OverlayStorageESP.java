package pm.n2.tangerine.render;

import com.adryd.cauldron.api.render.helper.OverlayRendererBase;
import com.adryd.cauldron.api.render.helper.RenderObject;
import com.adryd.cauldron.api.render.util.LineDrawing;
import com.adryd.cauldron.api.util.Color4f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.block.entity.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.visuals.StorageESPModule;
import java.util.HashMap;

public class OverlayStorageESP extends OverlayRendererBase {
	private HashMap<BlockPos, BlockEntity> blockEntities = new HashMap<>();

	private final HashMap<Class, Color4f> colors = new HashMap<>();
	private final HashMap<String, Color4f> dyeColors = new HashMap<>();
	private final Color4f SHULKER_COLOR = new Color4f(0.6f, 0.4f, 0.6f, 1f);

	public OverlayStorageESP() {
		this.renderObjects.add(new RenderObject(VertexFormat.DrawMode.LINES, VertexFormats.LINES, GameRenderer::getRenderTypeLinesShader));

		colors.put(ChestBlockEntity.class, RenderColors.OUTLINE_YELLOW);
		colors.put(TrappedChestBlockEntity.class, RenderColors.OUTLINE_RED);
		colors.put(BarrelBlockEntity.class, RenderColors.OUTLINE_BROWN);
		colors.put(EnderChestBlockEntity.class, RenderColors.OUTLINE_PURPLE);
		colors.put(FurnaceBlockEntity.class, RenderColors.OUTLINE_LIGHT_GRAY);
		colors.put(BlastFurnaceBlockEntity.class, RenderColors.OUTLINE_LIGHT_GRAY);
		colors.put(SmokerBlockEntity.class, RenderColors.OUTLINE_LIGHT_GRAY);
		colors.put(DispenserBlockEntity.class, RenderColors.OUTLINE_LIGHT_GRAY);
		colors.put(DropperBlockEntity.class, RenderColors.OUTLINE_LIGHT_GRAY);
		colors.put(HopperBlockEntity.class, RenderColors.OUTLINE_DARK_GRAY);

		for (var color : DyeColor.values()) {
			var components = color.getColorComponents();
			var color4f = new Color4f(components[0], components[1], components[2], 1f);
			dyeColors.put(color.getName(), color4f);
		}
	}

	@Override
	public void render(MatrixStack matrices, Matrix4f positionMatrix, float tickDelta) {
		RenderSystem.disableDepthTest();
		RenderSystem.lineWidth(1f);

		super.render(matrices, positionMatrix, tickDelta);
	}

	private Color4f getColor(BlockEntity be) {
		var out = RenderColors.OUTLINE_WHITE;
		var clazz = be.getClass();

		for (var targetClass : colors.keySet()) {
			if (clazz.isAssignableFrom(targetClass)) {
				out = colors.get(targetClass);
				break;
			}
		}

		if (be instanceof ShulkerBoxBlockEntity shulkerBox) {
			var color = shulkerBox.getColor();
			return color != null ? dyeColors.get(color.getName()) : SHULKER_COLOR;
		}

		return out;
	}

	@Override
	public void update(MatrixStack matrices, Camera camera, float tickDelta) {
		var renderLines = this.renderObjects.get(0);
		var linesBuf = renderLines.startBuffer();

		var be = blockEntities.clone();
		for (var be2 : blockEntities.values()) {
			LineDrawing.drawBox(new Box(be2.getPos()), getColor(be2), linesBuf);
		}

		renderLines.endBuffer();
	}

	@Override
	public boolean shouldRender() {
		var storageESP = (StorageESPModule) Tangerine.MODULE_MANAGER.get(StorageESPModule.class);
		return storageESP.enabled;
	}

	@Override
	public boolean shouldUpdate() {
		var storageESP = (StorageESPModule) Tangerine.MODULE_MANAGER.get(StorageESPModule.class);
		var be = (HashMap) storageESP.blockEntities.clone();
		if (!be.equals(this.blockEntities)) {
			this.blockEntities = be;
			return true;
		}

		return false;
	}
}
