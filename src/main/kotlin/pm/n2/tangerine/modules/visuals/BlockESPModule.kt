package pm.n2.tangerine.modules.visuals

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.WorldChunk
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.ListConfigOption
import pm.n2.tangerine.config.MapConfigOption
import pm.n2.tangerine.gui.GuiUtils
import pm.n2.tangerine.gui.renderables.ConfigWindow
import pm.n2.tangerine.mixin.ClientChunkManagerAccessor
import pm.n2.tangerine.mixin.ClientChunkMapAccessor
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.render.RenderUtils
import java.awt.Color

object BlockESPModule : Module("block_esp", ModuleCategory.VISUALS) {
    val blocks = ListConfigOption(id, "blocks", Identifier::class.java, mutableListOf())
    val blockColors = MapConfigOption(id, "block_colors", mutableMapOf(), Identifier::class.java, Color::class.java)
    val positions = mutableListOf<BlockPos>()

    override val configOptions = listOf(blocks, blockColors)
    override val configWindow = BlockESPConfigWindow()

    override fun onEnabled() {
        Tangerine.taskManager.run { search(MinecraftClient.getInstance().world) }
    }

    private fun matches(block: Block): Boolean {
        val blockKey = block.asItem().defaultStack.holder.key
        if (blockKey.isEmpty) return false
        val path = blockKey.get().value.path

        for (identifier in blocks.value) {
            if (identifier.path == path) return true
        }

        return false
    }

    fun resolveColor(pos: BlockPos): Color {
        val world = MinecraftClient.getInstance().world ?: return RenderUtils.white
        val block = world.getBlockState(pos).block

        val blockKey = block.asItem().defaultStack.holder.key
        if (blockKey.isEmpty) return RenderUtils.white
        val identifier = blockKey.get().value

        for ((id, color) in blockColors.value) {
            if (id.path == identifier.path && id.namespace == identifier.namespace) {
                return color
            }
        }

        return RenderUtils.white
    }

    fun searchChunk(chunk: WorldChunk?) {
        if (chunk == null) return
        val chunkX = chunk.pos.x * 16
        val chunkZ = chunk.pos.z * 16

        for (x in chunkX until (chunkX + 16)) {
            for (z in chunkZ until (chunkZ + 16)) {
                for (y in -64 until 320) {
                    val pos = BlockPos(x, y, z)
                    val block = chunk.getBlockState(pos).block

                    if (matches(block)) {
                        positions.add(pos)
                    }
                }
            }
        }
    }

    // used by mixins
    fun searchChunkSync(chunk: WorldChunk?) {
        Tangerine.taskManager.run { searchChunk(chunk) }
    }

    fun unloadChunk(startX: Int, startZ: Int) {
        Tangerine.taskManager.run {
            val positionsCloned = positions.toMutableList()
            for (position in positionsCloned) {
                if (
                    position.x in startX until (startX + 16)
                    && position.z in startZ until (startZ + 16)
                ) {
                    positions.remove(position)
                }
            }
        }
    }

    fun handleUpdate(pos: BlockPos, state: BlockState) {
        val matches = matches(state.block)
        val previouslyMatches = positions.contains(pos)

        if (matches && !previouslyMatches) {
            positions.add(pos)
        } else if (!matches && previouslyMatches) {
            positions.remove(pos)
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    fun search(world: ClientWorld?) {
        if (world == null || !enabled.value) return

        val chunkManager = world.chunkManager

        val chunkMap = (chunkManager as ClientChunkManagerAccessor).chunks
        val chunks = (chunkMap as ClientChunkMapAccessor).chunks

        positions.clear()

        for (i in 0 until chunks.length()) {
            searchChunk(chunks[i])
        }
    }

    class BlockESPConfigWindow : ConfigWindow(BlockESPModule) {
        private var string = ImString()

        override fun drawConfig() {
            val blocks = blocks.value.toMutableList()
            for (identifier in blocks) {
                if (ImGui.button("-##${identifier.path}")) {
                    BlockESPModule.blocks.value.remove(identifier)
                    Tangerine.taskManager.run { search(MinecraftClient.getInstance().world) }
                }

                ImGui.sameLine()
                val color = blockColors.value[identifier] ?: RenderUtils.white
                val ret = GuiUtils.colorPicker("##BlockESPColor${identifier.path}", color)
                if (ret != null) {
                    blockColors.value[identifier] = ret
                }

                ImGui.sameLine()
                ImGui.textUnformatted(identifier.path)
            }

            ImGui.inputText("Add block", string)

            ImGui.sameLine()
            if (ImGui.button("+")) {
                try {
                    BlockESPModule.blocks.value.add(Identifier("minecraft", string.get()))
                    Tangerine.taskManager.run { search(MinecraftClient.getInstance().world) }
                } catch (e: Exception) {
                    Tangerine.logger.error("Failed to add block", e)
                } finally {
                    string.clear()
                }
            }
        }
    }
}
