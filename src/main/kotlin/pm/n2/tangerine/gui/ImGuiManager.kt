package pm.n2.tangerine.gui

import gay.eviee.imguiquilt.interfaces.Theme
import imgui.ImFont
import imgui.ImGui

class ImGuiManager {
    companion object {
        private var font: ImFont? = null

        var theme: Theme = object : Theme {
            override fun preRender() {
                if (font != null) ImGui.pushFont(font)
            }

            override fun postRender() {
                if (font != null) ImGui.popFont()
            }
        }
    }

    val renderables = mutableListOf<TangerineRenderable>()

    fun addRenderable(renderable: TangerineRenderable) {
        renderable.manager = this
        renderables.add(renderable)
    }

    fun get(name: String): TangerineRenderable? {
        for (renderable in renderables) {
            if (renderable.name == name) return renderable
        }

        return null
    }

    fun setFont(font: ImFont) {
        ImGuiManager.font = font
    }
}
