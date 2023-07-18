package pm.n2.tangerine.config

import com.google.gson.JsonElement
import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import net.minecraft.client.resource.language.I18n
import org.lwjgl.glfw.GLFW
import pm.n2.tangerine.managers.KeyboardManager

abstract class ConfigOption<T>(open val group: String, open val name: String, open var value: T) {
    var keybind: ConfigKeybind? = null
    var valueCallback: ((T) -> Unit)? = null

    open fun set(value: T) {
        this.value = value
        this.valueCallback?.invoke(value)
    }

    open fun onKeybind() {}

    open fun parse(json: JsonElement) {
        value = TangerineConfig.GSON.fromJson(json, value!!::class.java)
    }

    open fun write() = TangerineConfig.GSON.toJsonTree(value, value!!::class.java)


    fun openHotkeyMenu() = ImGui.openPopup(I18n.translate("tangerine.ui.config.set_keybind") + "##${group}.${name}")
    fun drawHotkeyMenu() {
        val flags = (ImGuiWindowFlags.NoCollapse
            or ImGuiWindowFlags.NoResize
            or ImGuiWindowFlags.NoMove
            or ImGuiWindowFlags.AlwaysAutoResize)

        if (ImGui.beginPopupModal(
                I18n.translate("tangerine.ui.config.set_keybind") + "##${group}.${name}",
                ImBoolean(true),
                flags
            )
        ) {
            ImGui.text(I18n.translate("tangerine.ui.config.setting_keybind"))

            if (ImGui.button(I18n.translate("tangerine.ui.config.clear_keybind"))) {
                keybind = null
                TangerineConfig.write()
                ImGui.closeCurrentPopup()
            }

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
                    keybind = ConfigKeybind(i, shiftDown, controlDown, altDown)
                    TangerineConfig.write()
                    ImGui.closeCurrentPopup()
                    break
                }
            }

            ImGui.endPopup()
        }
    }
}
