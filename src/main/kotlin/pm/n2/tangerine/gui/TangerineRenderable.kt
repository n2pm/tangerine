package pm.n2.tangerine.gui

import gay.eviee.imguiquilt.interfaces.Renderable

open class TangerineRenderable(var name: String) {
    var enabled = true

    constructor(name: String, enabled: Boolean) : this(name) {
        this.enabled = enabled
    }

    val renderable = object : Renderable {
        override fun getName() = this@TangerineRenderable.name
        override fun getTheme() = ImGuiManager.theme
        override fun render() {
            if (enabled) draw()
        }
    }

    open fun draw() {}
}
