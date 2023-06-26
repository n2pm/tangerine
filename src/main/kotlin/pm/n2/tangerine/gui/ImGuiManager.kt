package pm.n2.tangerine.gui

import gay.eviee.imguiquilt.interfaces.Theme
import imgui.ImFont
import imgui.ImGui
import pm.n2.tangerine.gui.renderables.AboutWindow
import pm.n2.tangerine.gui.renderables.DemoWindow
import pm.n2.tangerine.gui.renderables.MenuBar

object ImGuiManager {
    private var font: ImFont? = null
    lateinit var fontDefault: ImFont
    lateinit var fontUnifont: ImFont

    val renderables = mutableListOf<TangerineRenderable>()

    init {
        addRenderable(MenuBar)
        addRenderable(DemoWindow)
        addRenderable(AboutWindow)
    }

    var theme: Theme = object : Theme {
        override fun preRender() {
            if (font != null) ImGui.pushFont(font)
        }

        override fun postRender() {
            if (font != null) ImGui.popFont()
        }
    }


    fun addRenderable(renderable: TangerineRenderable) {
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
