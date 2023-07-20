package pm.n2.tangerine.gui

import imgui.ImGui
import net.minecraft.text.Text
import pm.n2.hajlib.imgui.ImGuiScreen
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.TangerineConfig
import pm.n2.tangerine.managers.ImGuiManager

object TangerineScreen : ImGuiScreen(Text.of("Tangerine")) {
    override fun shouldPause() = false

    override fun drawImGui() {
        ImGui.pushFont(ImGuiManager.font)

        for (renderable in ImGuiManager.renderables) {
            if (!renderable.enabled) continue

            try {
                renderable.draw()
            } catch (e: Exception) {
                Tangerine.logger.error("Error while drawing renderable ${renderable.name}", e)
            }
        }

        ImGui.popFont()
    }

    override fun close() {
        super.close()
        TangerineConfig.write()
    }
}
