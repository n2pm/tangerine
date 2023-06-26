package pm.n2.tangerine.render

import com.adryd.cauldron.api.render.helper.OverlayRendererBase
import com.adryd.cauldron.api.render.helper.RenderObject
import com.adryd.cauldron.api.render.util.LineDrawing
import com.adryd.cauldron.api.util.Color4f
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormats
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d
import pm.n2.tangerine.modules.visuals.TracersModule

object OverlayTracers : OverlayRendererBase() {
    init {
        renderObjects.add(
            RenderObject(
                VertexFormat.DrawMode.LINES,
                VertexFormats.LINES
            ) { GameRenderer.getRenderTypeLinesShader() })
    }

    override fun render(tickDelta: Float, camera: Camera?) {
        RenderSystem.disableDepthTest()
        RenderSystem.lineWidth(1f)
        super.render(tickDelta, camera)
    }

    override fun update(matrices: MatrixStack?, camera: Camera?, tickDelta: Float) {
        val renderLines = renderObjects[0]
        val linesBuf = renderLines.startBuffer()

        val mc = MinecraftClient.getInstance()
        val player = mc.player
        val entities = mc.world!!.entities

        val tracersModule = TracersModule

        for (entity in entities) {
            if (entity === player) continue
            val color = getColor(entity) ?: continue
            val entityPos = entity.pos

            // shh
            var cameraPos = Vec3d(0.0, 0.0, 75.0)
            cameraPos = cameraPos.rotateX(-Math.toRadians(mc.gameRenderer.camera.pitch.toDouble()).toFloat())
            cameraPos = cameraPos.rotateY(-Math.toRadians(mc.gameRenderer.camera.yaw.toDouble()).toFloat())
            cameraPos = cameraPos.add(mc.cameraEntity!!.eyePos)

            LineDrawing.drawLine(
                entityPos.x,
                entityPos.y,
                entityPos.z,
                cameraPos.x,
                cameraPos.y,
                cameraPos.z,
                color,
                camera,
                linesBuf
            )

            if (tracersModule.drawStem.booleanValue) {
                val entityHeight = entity.height
                LineDrawing.drawLine(
                    entityPos.x,
                    entityPos.y + entityHeight,
                    entityPos.z,
                    entityPos.x,
                    entityPos.y,
                    entityPos.z,
                    color,
                    camera,
                    linesBuf
                )
            }
        }

        renderLines.endBuffer(camera)
    }

    private fun getColor(entity: Entity): Color4f? {
        val tracersModule = TracersModule

        if (entity is PlayerEntity) return if (tracersModule.drawPlayers.booleanValue) RenderColors.LIGHT_BLUE else null
        if (entity is PassiveEntity) return if (tracersModule.drawFriendly.booleanValue) RenderColors.OUTLINE_GREEN else null
        if (entity is HostileEntity) {
            return if (entity is Angerable) {
                if (tracersModule.drawPassive.booleanValue) RenderColors.OUTLINE_YELLOW else null
            } else {
                if (tracersModule.drawHostile.booleanValue) RenderColors.OUTLINE_RED else null
            }
        }
        if (entity is ItemEntity) return if (tracersModule.drawItems.booleanValue) RenderColors.OUTLINE_LIGHT_GRAY else null
        return if (tracersModule.drawOthers.booleanValue) RenderColors.OUTLINE_DARK_GRAY else null
    }

    override fun shouldRender(): Boolean = TracersModule.enabled.booleanValue
}
