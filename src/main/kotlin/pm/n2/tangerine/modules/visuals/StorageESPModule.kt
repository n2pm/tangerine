package pm.n2.tangerine.modules.visuals

import com.adryd.cauldron.api.render.helper.OverlayRenderManager
import net.minecraft.block.entity.*
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import pm.n2.tangerine.mixin.ClientChunkManagerAccessor
import pm.n2.tangerine.mixin.ClientChunkMapAccessor
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.render.OverlayStorageESP

class StorageESPModule : Module("storage_esp", "Storage ESP", "Highlights storage blocks", ModuleCategory.VISUALS) {
    var blockEntities = HashMap<BlockPos, BlockEntity>()

    init {
        OverlayRenderManager.addRenderer(OverlayStorageESP())
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun onEndTick(mc: MinecraftClient) {
        val world = mc.world
        if (mc.world == null || !enabled.booleanValue) return

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
