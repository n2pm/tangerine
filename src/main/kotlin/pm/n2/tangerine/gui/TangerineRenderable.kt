package pm.n2.tangerine.gui

open class TangerineRenderable(var name: String) {
    var enabled = true

    constructor(name: String, enabled: Boolean) : this(name) {
        this.enabled = enabled
    }

    open fun draw() {}
}
