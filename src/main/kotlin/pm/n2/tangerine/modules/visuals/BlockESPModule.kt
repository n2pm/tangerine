package pm.n2.tangerine.modules.visuals

import com.adryd.cauldron.api.render.helper.OverlayRenderManager
import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import pm.n2.tangerine.gui.renderables.ConfigWindow
import pm.n2.tangerine.mixin.ClientChunkManagerAccessor
import pm.n2.tangerine.mixin.ClientChunkMapAccessor
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.render.OverlayBlockESP

object BlockESPModule : Module(
    "block_esp",
    "Block ESP",
    "Set certain blocks to glow or not",
    ModuleCategory.VISUALS
) {
    val blocks = mutableListOf<Identifier>()
    val positions = mutableListOf<BlockPos>()

    override val configWindow = BlockESPConfigWindow()

    init {
        OverlayRenderManager.addRenderer(OverlayBlockESP)
    }

    override fun onEnabled() {
        search(MinecraftClient.getInstance().world)
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

    fun matches(block: Block): Boolean {
        val blockKey = block.asItem().defaultStack.holder.key
        if (blockKey.isEmpty) return false
        val path = blockKey.get().value.path

        for (identifier in blocks) {
            if (identifier.path == path) return true
        }

        return false
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    fun search(world: ClientWorld?) {
        if (world == null || !enabled.booleanValue) return

        val chunkManager = world.chunkManager

        val chunkMap = (chunkManager as ClientChunkManagerAccessor).chunks
        val chunks = (chunkMap as ClientChunkMapAccessor).chunks

        positions.clear()

        for (i in 0 until chunks.length()) {
            val chunk = chunks[i]
            if (chunk != null) {
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
        }
    }

    class BlockESPConfigWindow() : ConfigWindow(BlockESPModule) {
        private var string = ImString()

        override fun drawConfig() {
            val blocks = blocks.toMutableList()
            for (identifier in blocks) {
                ImGui.textUnformatted(identifier.path)
                ImGui.sameLine()
                if (ImGui.button("-")) {
                    blocks.remove(identifier)
                    search(MinecraftClient.getInstance().world)
                }
            }

            ImGui.inputText("Add block", string, ImGuiInputTextFlags.CallbackResize)

            ImGui.sameLine()
            if (ImGui.button("+")) {
                try {
                    blocks.add(Identifier("minecraft", string.get()))
                    search(MinecraftClient.getInstance().world)
                } catch (_: Exception) {
                } finally {
                    string.clear()
                }
            }
        }
    }
}
