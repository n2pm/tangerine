package pm.n2.tangerine.gui.renderables

import com.adryd.cauldron.api.config.ConfigBoolean
import com.adryd.cauldron.api.config.ConfigDouble
import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import net.minecraft.client.resource.language.I18n
import org.lwjgl.glfw.GLFW
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.modules.Module

class ConfigWindow(val module: Module) : TangerineRenderable("ConfigWindow##${module.name}", false) {
    init {
        Tangerine.imguiManager.addRenderable(this)
    }

    override fun draw() {
        var enabled = ImBoolean(this.enabled)

        if (ImGui.begin(module.name, enabled)) {
            if (ImGui.checkbox("Enabled", module.enabled.booleanValue)) {
                module.enabled.toggle()
                if (module.enabled.booleanValue) {
                    module.onEnabled()
                } else {
                    module.onDisabled()
                }
            }

            ImGui.text("Current keybind: " + GLFW.glfwGetKeyName(module.keybind.integerValue, 0))
            ImGui.sameLine()
            if (ImGui.button("Assign keybind")) {
                ImGui.openPopup("Assign keybind##" + module.name)
            }

            val configOptions = module.getConfigOptions()
            for (config in configOptions) {
                if (config is ConfigBoolean) {
                    if (ImGui.checkbox(I18n.translate("tangerine.config." + config.getKey()), config.booleanValue)) {
                        config.toggle()
                    }
                }
                if (config is ConfigDouble) {
                    val doubleValue: Double = config.doubleValue
                    val doubleValueArr = floatArrayOf(doubleValue.toFloat())
                    if (ImGui.dragFloat(I18n.translate("tangerine.config." + config.getKey()), doubleValueArr, 0.1f, config.minValue.toFloat(), config.maxValue.toFloat())) {
                        config.doubleValue = doubleValueArr[0].toDouble()
                    }
                }
            }

            val flags = (ImGuiWindowFlags.NoCollapse
                    or ImGuiWindowFlags.NoResize
                    or ImGuiWindowFlags.NoMove
                    or ImGuiWindowFlags.AlwaysAutoResize)
            if (ImGui.beginPopupModal("Assign keybind##" + module.name, ImBoolean(true), flags)) {
                ImGui.text("Press a key to assign it to this module.")

                // 0, escape
                val blacklistedKeys = listOf(0, GLFW.GLFW_KEY_ESCAPE)

                for (i in 0..GLFW.GLFW_KEY_LAST) {
                    if (blacklistedKeys.contains(i)) continue
                    if (Tangerine.keyboardManager.isKeyPressed(i)) {
                        Tangerine.logger.info("Keybind for {} set to {}", module.name, i)
                        module.keybind.integerValue = i
                        ImGui.closeCurrentPopup()
                        break
                    }
                }
                ImGui.endPopup()
            }
        }

        ImGui.end()
        this.enabled = enabled.get()
    }
}
