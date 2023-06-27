package pm.n2.tangerine.modules

import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.gui.renderables.ConfigWindow

@Suppress("LeakingThis")
abstract class Module(
    val id: String,
    val name: String,
    val description: String,
    val category: ModuleCategory
) {
    val enabled = BooleanConfigOption(id, "enabled",false)
    open val configOptions = listOf<ConfigOption<*>>()
    open val configWindow = ConfigWindow(this)

    open fun showConfigWindow() {
        configWindow.enabled = true
    }

    open fun onEnabled() = Unit
    open fun onDisabled() = Unit
}
