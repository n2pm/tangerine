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
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import pm.n2.tangerine.modules.visuals.StorageESPModule
import java.awt.Color

object OverlayStorageESP : OverlayRendererBase() {
    private var blockEntities = HashMap<BlockPos, BlockEntity>()
    private var configHash = 0
    private val dyeColors = HashMap<String, Color>()

    init {
        renderObjects.add(
            RenderObject(
                VertexFormat.DrawMode.LINES,
                VertexFormats.LINES
            ) { GameRenderer.getRenderTypeLinesShader() })

        for (color in DyeColor.values()) {
            val components = color.colorComponents
            val dyeColor = Color(components[0], components[1], components[2], 1f)
            dyeColors[color.getName()] = dyeColor
        }
    }

    override fun render(tickDelta: Float, camera: Camera?) {
        RenderSystem.disableDepthTest()
        RenderSystem.lineWidth(1f)
        super.render(tickDelta, camera)
    }

    private fun getColor(be: BlockEntity): Color {
        var out = RenderUtils.white

        val colors = mapOf(
            ChestBlockEntity::class to StorageESPModule.chestColor,
            TrappedChestBlockEntity::class to StorageESPModule.trappedChestColor,
            BarrelBlockEntity::class to StorageESPModule.barrelColor,
            EnderChestBlockEntity::class to StorageESPModule.enderChestColor,
            FurnaceBlockEntity::class to StorageESPModule.furnaceColor,
            BlastFurnaceBlockEntity::class to StorageESPModule.blastFurnaceColor,
            SmokerBlockEntity::class to StorageESPModule.smokerColor,
            DispenserBlockEntity::class to StorageESPModule.dispenserColor,
            DropperBlockEntity::class to StorageESPModule.dropperColor,
            HopperBlockEntity::class to StorageESPModule.hopperColor,
        )

        for (targetClass in colors.keys) {
            if (targetClass.java.isAssignableFrom(be.javaClass)) {
                val newColor = colors[targetClass]?.value
                if (newColor != null) out = newColor
            }
        }

        if (be is ShulkerBoxBlockEntity) {
            val color = be.color
            if (color != null) {
                val dyeColor = dyeColors[color.getName()]
                if (dyeColor != null) out = dyeColor
            } else {
                out = RenderUtils.purple
            }
        }

        return out
    }

    override fun update(matrices: MatrixStack?, camera: Camera?, tickDelta: Float) {
        val renderLines = renderObjects[0]
        val linesBuf = renderLines.startBuffer()
        for (be in blockEntities.values) {
            var canDraw = false

            var mapping = mapOf(
                ChestBlockEntity::class to StorageESPModule.drawChests,
                TrappedChestBlockEntity::class to StorageESPModule.drawTrappedChests,
                BarrelBlockEntity::class to StorageESPModule.drawBarrels,
                EnderChestBlockEntity::class to StorageESPModule.drawEnderChests,
                FurnaceBlockEntity::class to StorageESPModule.drawFurnaces,
                BlastFurnaceBlockEntity::class to StorageESPModule.drawBlastFurnaces,
                SmokerBlockEntity::class to StorageESPModule.drawSmokers,
                DispenserBlockEntity::class to StorageESPModule.drawDispensers,
                DropperBlockEntity::class to StorageESPModule.drawDroppers,
                HopperBlockEntity::class to StorageESPModule.drawHoppers,
                ShulkerBoxBlockEntity::class to StorageESPModule.drawShulkers
            )

            for ((block, option) in mapping) {
                if (block.java.isAssignableFrom(be.javaClass)) {
                    canDraw = option.value
                }
            }

            if (!canDraw) continue

            LineDrawing.drawBox(
                Box(be.pos),
                RenderUtils.colorToCauldron(getColor(be)),
                camera, linesBuf
            )
        }
        renderLines.endBuffer(camera)
    }

    override fun shouldRender(): Boolean {
        return StorageESPModule.enabled.value && blockEntities.size > 0
    }

    override fun shouldUpdate(camera: Camera?): Boolean {
        val be = StorageESPModule.blockEntities
        if (be != this.blockEntities) {
            this.blockEntities = be
            return true
        }

        // I'm not actually sold this works, but I don't care
        val hash = StorageESPModule.configOptions.map { it.value }.hashCode()
        if (this.configHash != hash) {
            this.configHash = hash
            return true
        }

        return false
    }
}
