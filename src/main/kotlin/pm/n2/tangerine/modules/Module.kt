@file:Suppress("LeakingThis")

package pm.n2.tangerine.modules

import com.adryd.cauldron.api.config.ConfigBoolean
import com.adryd.cauldron.api.config.ConfigInteger
import com.adryd.cauldron.api.config.IConfigOption
import com.google.common.collect.ImmutableList
import net.minecraft.client.MinecraftClient
import pm.n2.tangerine.gui.renderables.ConfigWindow

open class Module(val id: String, val name: String, val description: String, val category: ModuleCategory) {
    public var enabled = ConfigBoolean("$id.enabled", false)
    public var keybind = ConfigInteger("$id.keybind", 0, 0, 255)

    open val configWindow = ConfigWindow(this)

    fun showConfigWindow() {
        configWindow.enabled = true
    }

    open fun onEnabled() {}
    open fun onDisabled() {}
    open fun onStartTick(mc: MinecraftClient) {}
    open fun onEndTick(mc: MinecraftClient) {}
    open fun getConfigOptions(): ImmutableList<IConfigOption> = ImmutableList.of()
}
