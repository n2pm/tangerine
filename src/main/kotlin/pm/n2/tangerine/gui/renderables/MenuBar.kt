package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import net.minecraft.client.resource.language.I18n
import pm.n2.tangerine.managers.ModuleManager
import pm.n2.tangerine.gui.TangerineScreen
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.managers.ImGuiManager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object MenuBar : TangerineRenderable("MenuBar") {
    private fun drawMenuTab(name: String, modules: List<Module>) {
        if (ImGui.beginMenu(name)) {
            for (module in modules) {
                val moduleName = I18n.translate("tangerine.module.${module.id}.name")
                val moduleDescription = I18n.translate("tangerine.module.${module.id}.description")
                if (module.shouldHideEnabled) {
                    if (ImGui.menuItem(moduleName)) module.showConfigWindow()
                } else {
                    if (ImGui.menuItem(moduleName, "", module.enabled.value)) {
                        ModuleManager.toggle(module)
                    }
                }

                if (ImGui.beginPopupContextItem()) {
                    if (ImGui.menuItem(I18n.translate("tangerine.ui.config"))) {
                        module.showConfigWindow()
                    }

                    ImGui.endPopup()
                }

                if (ImGui.isItemHovered()) {
                    ImGui.beginTooltip()
                    ImGui.text(moduleDescription)
                    ImGui.endTooltip()
                }
            }
            ImGui.endMenu()
        }
    }

    override fun draw() {
        var openKeybindMenu = false
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Tangerine")) {
                val demoWindow = ImGuiManager.get("DemoWindow")!!
                val aboutWindow = ImGuiManager.get("AboutWindow")!!

                if (ImGui.menuItem(I18n.translate("tangerine.ui.about.name"), "", aboutWindow.enabled)) {
                    aboutWindow.enabled = !aboutWindow.enabled
                }

                if (ImGui.menuItem(I18n.translate("tangerine.ui.demo.name"), "", demoWindow.enabled)) {
                    demoWindow.enabled = !demoWindow.enabled
                }

                if (ImGui.menuItem(I18n.translate("tangerine.ui.menu.set_menu_keybind"), "")) {
                    openKeybindMenu = true
                }

                if (ImGui.isItemHovered()) {
                    val keybindStr =
                        ImGuiManager.opened.keybind?.toString() ?: I18n.translate("tangerine.ui.config.no_keybind")
                    ImGui.setTooltip(I18n.translate("tangerine.ui.config.current_keybind", keybindStr))
                }

                if (ImGui.menuItem("Close menu bar")) {
                    TangerineScreen.close()
                }

                ImGui.endMenu()
            }

            for (category in ModuleManager.categories) {
                drawMenuTab(
                    I18n.translate("tangerine.category.${category.id}"),
                    ModuleManager.getModulesByCategory(category)
                )
            }

            ImGui.endMainMenuBar()
        }

        if (openKeybindMenu) ImGuiManager.opened.openHotkeyMenu()
        ImGuiManager.opened.drawHotkeyMenu()
    }
}
