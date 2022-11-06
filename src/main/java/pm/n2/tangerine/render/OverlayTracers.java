package pm.n2.tangerine.render;

import com.adryd.cauldron.api.render.helper.OverlayRendererBase;
import com.adryd.cauldron.api.render.helper.RenderObject;
import com.adryd.cauldron.api.render.util.LineDrawing;
import com.adryd.cauldron.api.util.Color4f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.visuals.TracersModule;

public class OverlayTracers extends OverlayRendererBase {
	public OverlayTracers() {
		this.renderObjects.add(new RenderObject(VertexFormat.DrawMode.LINES, VertexFormats.LINES, GameRenderer::getRenderTypeLinesShader));
	}

	@Override
	public void render(MatrixStack matrices, Matrix4f positionMatrix, float tickDelta) {
		RenderSystem.disableDepthTest();
		RenderSystem.lineWidth(1f);

		super.render(matrices, positionMatrix, tickDelta);
	}

	@Override
	public void update(MatrixStack matrices, Camera camera, float tickDelta) {
		var renderLines = this.renderObjects.get(0);
		var linesBuf = renderLines.startBuffer();

		var mc = MinecraftClient.getInstance();
		var player = mc.player;
		var entities = mc.world.getEntities();
		var tracersModule = (TracersModule) Tangerine.MODULE_MANAGER.get(TracersModule.class);

		for (var entity : entities) {
			if (entity == player) continue;

			var color = getColor(entity);
			if (color == null) continue;

			var entityPos = entity.getPos();
			// shh
			var cameraPos = new Vec3d(0, 0, 75);

			cameraPos = cameraPos.rotateX((float) -Math.toRadians(mc.gameRenderer.getCamera().getPitch()));
			cameraPos = cameraPos.rotateY((float) -Math.toRadians(mc.gameRenderer.getCamera().getYaw()));
			cameraPos = cameraPos.add(mc.cameraEntity.getEyePos());

			LineDrawing.drawLine(entityPos.x, entityPos.y, entityPos.z, cameraPos.x, cameraPos.y, cameraPos.z, color, linesBuf);

			if (tracersModule.drawStem.getBooleanValue()) {
				var entityHeight = entity.getHeight();
				LineDrawing.drawLine(entityPos.x, entityPos.y + entityHeight, entityPos.z, entityPos.x, entityPos.y, entityPos.z, color, linesBuf);
			}
		}

		renderLines.endBuffer();
	}

	private Color4f getColor(Entity entity) {
		var tracersModule = (TracersModule) Tangerine.MODULE_MANAGER.get(TracersModule.class);

		if (entity instanceof PlayerEntity) return tracersModule.drawPlayers.getBooleanValue() ? RenderColors.LIGHT_BLUE : null;
		if (entity instanceof PassiveEntity) return tracersModule.drawFriendly.getBooleanValue() ? RenderColors.OUTLINE_GREEN : null;
		if (entity instanceof HostileEntity) {
			if (entity instanceof Angerable) {
				return tracersModule.drawPassive.getBooleanValue() ? RenderColors.OUTLINE_YELLOW : null;
			} else {
				return tracersModule.drawHostile.getBooleanValue() ? RenderColors.OUTLINE_RED : null;
			}
		}
		if (entity instanceof ItemEntity) return tracersModule.drawItems.getBooleanValue() ? RenderColors.OUTLINE_LIGHT_GRAY : null;

		return tracersModule.drawOthers.getBooleanValue() ? RenderColors.OUTLINE_DARK_GRAY : null;
	}

	@Override
	public boolean shouldUpdate() {
		return super.shouldUpdate();
	}

	@Override
	public boolean shouldRender() {
		return Tangerine.MODULE_MANAGER.get(TracersModule.class).enabled.getBooleanValue();
	}
}
