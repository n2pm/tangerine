package pm.n2.tangerine.modules.visuals

import net.minecraft.block.entity.*
import net.minecraft.util.math.BlockPos
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.ColorConfigOption
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.mixin.ClientChunkManagerAccessor
import pm.n2.tangerine.mixin.ClientChunkMapAccessor
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.render.RenderUtils

object StorageESPModule : Module("storage_esp", ModuleCategory.VISUALS) {
    var blockEntities = HashMap<BlockPos, BlockEntity>()

    val chestColor = ColorConfigOption(id, "chest_color", RenderUtils.yellow)
    val trappedChestColor = ColorConfigOption(id, "trapped_chest_color", RenderUtils.red)
    val barrelColor = ColorConfigOption(id, "barrel_color", RenderUtils.brown)
    val enderChestColor = ColorConfigOption(id, "ender_chest_color", RenderUtils.purple)
    val furnaceColor = ColorConfigOption(id, "furnace_color", RenderUtils.grey)
    val blastFurnaceColor = ColorConfigOption(id, "blast_furnace_color", RenderUtils.grey)
    val smokerColor = ColorConfigOption(id, "smoker_color", RenderUtils.grey)
    val dispenserColor = ColorConfigOption(id, "dispenser_color", RenderUtils.grey)
    val dropperColor = ColorConfigOption(id, "dropper_color", RenderUtils.grey)
    val hopperColor = ColorConfigOption(id, "hopper_color", RenderUtils.grey)

    override val configOptions = listOf(
        chestColor,
        trappedChestColor,
        barrelColor,
        enderChestColor,
        furnaceColor,
        blastFurnaceColor,
        smokerColor,
        dispenserColor,
        dropperColor,
        hopperColor
    )

    @Suppress("CAST_NEVER_SUCCEEDS")
    @EventHandler
    fun onPostTick(event: TangerineEvent.PostTick) {
        val mc = Tangerine.mc
        val world = mc.world
        if (mc.world == null || !enabled.value) return

        val chunkManager = world!!.chunkManager

        val chunkMap = (chunkManager as ClientChunkManagerAccessor).chunks
        val chunks = (chunkMap as ClientChunkMapAccessor).chunks

        val allowedEntities = arrayOf<Class<*>>(
            ChestBlockEntity::class.java,
            TrappedChestBlockEntity::class.java,
            BarrelBlockEntity::class.java,
            EnderChestBlockEntity::class.java,
            ShulkerBoxBlockEntity::class.java,
            FurnaceBlockEntity::class.java,
            BlastFurnaceBlockEntity::class.java,
            SmokerBlockEntity::class.java,
            DispenserBlockEntity::class.java,
            DropperBlockEntity::class.java,
            HopperBlockEntity::class.java
        )

        val newBlockEntities = HashMap<BlockPos, BlockEntity>()
        for (i in 0 until chunks.length()) {
            val chunk = chunks[i]
            if (chunk != null) {
                val blockEntities = chunk.blockEntities
                for (be in blockEntities.values) {
                    for (targetClass in allowedEntities) {
                        if (be.javaClass.isAssignableFrom(targetClass)) {
                            newBlockEntities[be.pos] = be
                            break
                        }
                    }
                }
            }
        }

        blockEntities = newBlockEntities
    }
}
