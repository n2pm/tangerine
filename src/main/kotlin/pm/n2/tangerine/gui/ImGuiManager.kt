package pm.n2.tangerine.gui

import imgui.ImFont
import imgui.ImGui
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.config.TangerineConfig
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.gui.renderables.AboutWindow
import pm.n2.tangerine.gui.renderables.DemoWindow
import pm.n2.tangerine.gui.renderables.MenuBar

object ImGuiManager {
    var font: ImFont? = null
    lateinit var fontDefault: ImFont
    lateinit var fontUnifont: ImFont

    // Side effect of migrating from imgui-quilt: we have two types of renderables :woozy_face:
    val renderables = mutableListOf<TangerineRenderable>()
    val instantRenderables = mutableListOf<TangerineRenderable>()
    val opened = BooleanConfigOption("tangerine", "open", false)

    init {
        TangerineConfig.addConfigOptions(listOf(opened))
        Tangerine.eventManager.registerClass(this)
    }

    init {
        addRenderable(MenuBar)
        addRenderable(DemoWindow)
        addRenderable(AboutWindow)
    }

    fun addRenderable(renderable: TangerineRenderable) {
        if (renderables.contains(renderable)) return
        renderables.add(renderable)
    }

    fun addInstantRenderable(renderable: TangerineRenderable) {
        if (instantRenderables.contains(renderable)) return
        instantRenderables.add(renderable)
    }

    fun removeRenderable(renderable: TangerineRenderable) {
        if (!renderables.contains(renderable)) return
        renderables.remove(renderable)
    }

    fun removeInstantRenderable(renderable: TangerineRenderable) {
        if (!instantRenderables.contains(renderable)) return
        instantRenderables.remove(renderable)
    }

    fun get(name: String): TangerineRenderable? {
        for (renderable in renderables) {
            if (renderable.name == name) return renderable
        }

        return null
    }

    @EventHandler
    fun imGuiDraw(event: TangerineEvent.ImGuiDraw) {
        ImGui.pushFont(font)

        for (renderable in instantRenderables) {
            if (!renderable.enabled) continue

            try {
                renderable.draw()
            } catch (e: Exception) {
                Tangerine.logger.error("Error while drawing instant renderable ${renderable.name}", e)
            }
        }

        ImGui.popFont()
    }
}
