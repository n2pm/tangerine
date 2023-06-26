package pm.n2.tangerine.modules

import com.adryd.cauldron.api.config.ConfigBoolean
import com.adryd.cauldron.api.config.ConfigInteger
import com.adryd.cauldron.api.config.IConfigOption
import net.minecraft.client.MinecraftClient
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.gui.renderables.ConfigWindow

@Suppress("LeakingThis")
abstract class Module(
    val id: String,
    val name: String,
    val description: String,
    val category: ModuleCategory
) {
    val enabled = ConfigBoolean("$id.enabled", false)
    val keybind = ConfigInteger("$id.keybind", 0, 0, 255)
    open val configOptions = listOf<IConfigOption>()
    open val configWindow = ConfigWindow(this)

    open fun showConfigWindow() {
        configWindow.enabled = true
    }

    open fun onEnabled() = Unit
    open fun onDisabled() = Unit
}
