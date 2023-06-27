package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import net.minecraft.client.resource.language.I18n
import org.lwjgl.glfw.GLFW
import pm.n2.tangerine.KeyboardManager
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.*
import pm.n2.tangerine.core.managers.ModuleManager
import pm.n2.tangerine.gui.ImGuiManager
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.modules.Module

@Suppress("LeakingThis")
open class ConfigWindow(open val module: Module) : TangerineRenderable("ConfigWindow##${module.name}", false) {
    init {
        ImGuiManager.addRenderable(this)
    }

    private fun handleContextMenu(module: Module, option: ConfigOption<*>, cb: () -> Unit = {}) {
        val keybindPopup = "Set keybind##${option.group}.${option.name}"
        var isOpeningPopup = false

        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Set keybind##item.${option.group}.${option.name}")) {
                isOpeningPopup = true
            }

            cb()

            ImGui.endPopup()
        }

        // https://github.com/ocornut/imgui/issues/331
        if (isOpeningPopup) ImGui.openPopup(keybindPopup)

        val flags = (ImGuiWindowFlags.NoCollapse
                or ImGuiWindowFlags.NoResize
                or ImGuiWindowFlags.NoMove
                or ImGuiWindowFlags.AlwaysAutoResize)
        if (ImGui.beginPopupModal(keybindPopup, ImBoolean(true), flags)) {
            ImGui.text("Press a key to assign it to this module.")

            val blacklistedKeys = listOf(
                0,
                GLFW.GLFW_KEY_ESCAPE,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                GLFW.GLFW_KEY_LEFT_CONTROL,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                GLFW.GLFW_KEY_LEFT_ALT,
                GLFW.GLFW_KEY_RIGHT_ALT
            )

            val shiftDown =
                KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT)
            val controlDown =
                KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)
            val altDown =
                KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT)

            for (i in 0..GLFW.GLFW_KEY_LAST) {
                if (blacklistedKeys.contains(i)) continue
                if (KeyboardManager.isKeyPressed(i)) {
                    option.keybind = ConfigKeybind(i, shiftDown, controlDown, altDown)
                    TangerineConfig.write()
                    ImGui.closeCurrentPopup()
                    break
                }
            }

            ImGui.endPopup()
        }
    }

    override fun draw() {
        val enabled = ImBoolean(this.enabled)

        if (ImGui.begin(module.name, enabled)) {
            if (ImGui.checkbox("Enabled", module.enabled.value)) {
                ModuleManager.toggle(module)
            }
            handleContextMenu(module, module.enabled)

            drawConfig()


        }

        ImGui.end()
        this.enabled = enabled.get()
    }

    open fun drawConfig() {
        val configOptions = module.configOptions
        for (config in configOptions) {
            if (config is BooleanConfigOption) {
                if (ImGui.checkbox(I18n.translate("tangerine.config.${config.group}.${config.name}"), config.value)) {
                    config.toggle()
                }
            }

            if (config is DoubleConfigOption) {
                val doubleValue: Double = config.value
                val doubleValueArr = floatArrayOf(doubleValue.toFloat())
                if (ImGui.dragFloat(
                        I18n.translate("tangerine.config.${config.group}.${config.name}"),
                        doubleValueArr,
                        0.1f,
                        config.value.toFloat(),
                        config.value.toFloat()
                    )
                ) {
                    config.set(doubleValueArr[0].toDouble())
                }
            }

            handleContextMenu(module, config)
        }
    }
}
