package pm.n2.tangerine.render

import com.adryd.cauldron.api.render.helper.OverlayRendererBase
import com.adryd.cauldron.api.render.helper.RenderObject
import com.adryd.cauldron.api.render.util.LineDrawing
import com.adryd.cauldron.api.util.Color4f
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormats
import net.minecraft.block.entity.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.modules.visuals.StorageESPModule

class OverlayStorageESP : OverlayRendererBase() {
    private var blockEntities = HashMap<BlockPos, BlockEntity>()
    private val colors = HashMap<Class<*>, Color4f>()
    private val dyeColors = HashMap<String, Color4f>()

    init {
        renderObjects.add(RenderObject(VertexFormat.DrawMode.LINES, VertexFormats.LINES) { GameRenderer.getRenderTypeLinesShader() })

        colors[ChestBlockEntity::class.java] = RenderColors.OUTLINE_YELLOW
        colors[TrappedChestBlockEntity::class.java] = RenderColors.OUTLINE_RED
        colors[BarrelBlockEntity::class.java] = RenderColors.OUTLINE_BROWN
        colors[EnderChestBlockEntity::class.java] = RenderColors.OUTLINE_PURPLE
        colors[FurnaceBlockEntity::class.java] = RenderColors.OUTLINE_LIGHT_GRAY
        colors[BlastFurnaceBlockEntity::class.java] = RenderColors.OUTLINE_LIGHT_GRAY
        colors[SmokerBlockEntity::class.java] = RenderColors.OUTLINE_LIGHT_GRAY
        colors[DispenserBlockEntity::class.java] = RenderColors.OUTLINE_LIGHT_GRAY
        colors[DropperBlockEntity::class.java] = RenderColors.OUTLINE_LIGHT_GRAY
        colors[HopperBlockEntity::class.java] = RenderColors.OUTLINE_DARK_GRAY

        for (color in DyeColor.values()) {
            val components = color.colorComponents
            val color4f = Color4f(components[0], components[1], components[2], 1f)
            dyeColors[color.getName()] = color4f
        }
    }

    override fun render(tickDelta: Float, camera: Camera?) {
        RenderSystem.disableDepthTest()
        RenderSystem.lineWidth(1f)
        super.render(tickDelta, camera)
    }

    private fun getColor(be: BlockEntity): Color4f? {
        var out: Color4f? = RenderColors.OUTLINE_WHITE

        for (targetClass in colors.keys) {
            if (be.javaClass.isAssignableFrom(targetClass)) {
                out = colors[targetClass]
                break
            }
        }

        if (be is ShulkerBoxBlockEntity) {
            val color = be.color
            return if (color != null) dyeColors[color.getName()] else RenderColors.OUTLINE_OTHER_PURPLE
        }

        return out
    }

    override fun update(matrices: MatrixStack?, camera: Camera?, tickDelta: Float) {
        val renderLines = renderObjects[0]
        val linesBuf = renderLines.startBuffer()
        for (be in blockEntities.values) LineDrawing.drawBox(Box(be.pos), getColor(be), camera, linesBuf)
        renderLines.endBuffer(camera)
    }

    override fun shouldRender(): Boolean {
        val storageESP = Tangerine.moduleManager.get(StorageESPModule::class)
        return storageESP.enabled.booleanValue && blockEntities.size > 0
    }

    override fun shouldUpdate(camera: Camera?): Boolean {
        val storageESP = Tangerine.moduleManager.get(StorageESPModule::class)
        val be = storageESP.blockEntities
        if (be != this.blockEntities) {
            this.blockEntities = be
            return true
        }

        return false
    }
}
