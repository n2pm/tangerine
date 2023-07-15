package pm.n2.tangerine.modules

import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.gui.renderables.ConfigWindow

@Suppress("LeakingThis")
abstract class Module(
    val id: String,
    val category: ModuleCategory
) {
    val enabled = BooleanConfigOption(id, "enabled",false)
    open val configWindow = ConfigWindow(this)
    open val configOptions = listOf<ConfigOption<*>>()
    open val shouldHideEnabled = false

    open fun showConfigWindow() {
        configWindow.enabled = true
    }

    /**
     * Called after the module's config is loaded, regardless of enabled state.
     */
    open fun init() = Unit

    /**
     * Called when the module is toggled on during play.
     */
    open fun onEnabled() = Unit

    /**
     * Called when the module is toggled off during play.
     */
    open fun onDisabled() = Unit
}
