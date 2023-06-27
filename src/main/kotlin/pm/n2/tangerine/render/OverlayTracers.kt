package pm.n2.tangerine.render

import com.adryd.cauldron.api.render.helper.OverlayRendererBase
import com.adryd.cauldron.api.render.helper.RenderObject
import com.adryd.cauldron.api.render.util.LineDrawing
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
import java.awt.Color

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
                RenderUtils.colorToCauldron(color),
                camera,
                linesBuf
            )

            if (tracersModule.drawStem.value) {
                val entityHeight = entity.height
                LineDrawing.drawLine(
                    entityPos.x,
                    entityPos.y + entityHeight,
                    entityPos.z,
                    entityPos.x,
                    entityPos.y,
                    entityPos.z,
                    RenderUtils.colorToCauldron(color),
                    camera,
                    linesBuf
                )
            }
        }

        renderLines.endBuffer(camera)
    }

    private fun getColor(entity: Entity): Color? {
        val tracersModule = TracersModule

        return when (entity) {
            is PlayerEntity -> if (tracersModule.drawPlayers.value) TracersModule.playersColor.value else null
            is PassiveEntity -> if (tracersModule.drawFriendly.value) TracersModule.passiveColor.value else null
            is HostileEntity -> {
                if (entity is Angerable) {
                    if (tracersModule.drawPassive.value) tracersModule.angerableColor.value else null
                } else {
                    if (tracersModule.drawHostile.value) tracersModule.hostileColor.value else null
                }
            }

            is ItemEntity -> if (tracersModule.drawItems.value) tracersModule.itemColor.value else null
            else -> if (tracersModule.drawOthers.value) tracersModule.othersColor.value else null
        }
    }

    override fun shouldRender(): Boolean = TracersModule.enabled.value
}
