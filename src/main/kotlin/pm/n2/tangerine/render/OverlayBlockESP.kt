package pm.n2.tangerine.render

import com.adryd.cauldron.api.render.helper.OverlayRendererBase
import com.adryd.cauldron.api.render.helper.RenderObject
import com.adryd.cauldron.api.render.util.LineDrawing
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormats
import net.minecraft.block.entity.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.modules.visuals.BlockESPModule

class OverlayBlockESP : OverlayRendererBase() {
    var positions = mutableListOf<BlockPos>()

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
        for (pos in positions) LineDrawing.drawBox(Box(pos), RenderColors.OUTLINE_WHITE, camera, linesBuf)
        renderLines.endBuffer(camera)
    }

    override fun shouldRender(): Boolean {
        val blockESP = Tangerine.moduleManager.get(BlockESPModule::class)
        return blockESP.enabled.booleanValue && blockESP.positions.size > 0
    }

    override fun shouldUpdate(camera: Camera?): Boolean {
        val blockESP = Tangerine.moduleManager.get(BlockESPModule::class)
        val positions = blockESP.positions
        if (positions != this.positions) {
            this.positions = mutableListOf()
            this.positions.addAll(positions)
            return true
        }

        return false
    }
}
