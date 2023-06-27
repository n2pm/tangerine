package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.type.ImBoolean
import net.minecraft.client.resource.language.I18n
import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.config.ColorConfigOption
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.config.DoubleConfigOption
import pm.n2.tangerine.core.managers.ModuleManager
import pm.n2.tangerine.gui.GuiUtils
import pm.n2.tangerine.gui.ImGuiManager
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.modules.Module

@Suppress("LeakingThis")
open class ConfigWindow(open val module: Module) : TangerineRenderable("ConfigWindow##${module.id}", false) {
    init {
        ImGuiManager.addRenderable(this)
    }

    private fun handleContextMenu(module: Module, option: ConfigOption<*>, cb: () -> Unit = {}) {
        var isOpeningPopup = false

        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem(I18n.translate("tangerine.ui.config.set_keybind") + "##item.${option.group}.${option.name}")) {
                isOpeningPopup = true
            }

            if (ImGui.isItemHovered()) {
                val keybindStr = option.keybind?.toString() ?: I18n.translate("tangerine.ui.config.no_keybind")
                ImGui.setTooltip(I18n.translate("tangerine.ui.config.current_keybind", keybindStr))
            }

            cb()

            ImGui.endPopup()
        }

        // https://github.com/ocornut/imgui/issues/331
        if (isOpeningPopup) option.openHotkeyMenu()
        option.drawHotkeyMenu()
    }

    override fun draw() {
        val enabled = ImBoolean(this.enabled)

        if (ImGui.begin(I18n.translate("tangerine.module.${module.id}.name"), enabled)) {
            if (ImGui.checkbox(I18n.translate("tangerine.ui.config.enabled"), module.enabled.value)) {
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

            if (config is ColorConfigOption) {
                val ret = GuiUtils.colorPicker(
                    I18n.translate("tangerine.config.${config.group}.${config.name}"),
                    config.value
                )

                if (ret != null) {
                    config.value = ret
                }

                // Don't context menu this or shit crashes
                continue
            }

            handleContextMenu(module, config)
        }
    }
}
