package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import net.minecraft.client.resource.language.I18n
import pm.n2.tangerine.core.managers.ModuleManager
import pm.n2.tangerine.gui.ImGuiManager
import pm.n2.tangerine.gui.ImGuiScreen
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object MenuBar : TangerineRenderable("MenuBar") {
    private fun drawMenuTab(name: String, modules: List<Module>) {
        if (ImGui.beginMenu(name)) {
            for (module in modules) {
                if (ImGui.menuItem(I18n.translate("tangerine.module.${module.id}.name"), "", module.enabled.value)) {
                    ModuleManager.toggle(module)
                }

                if (ImGui.beginPopupContextItem()) {
                    if (ImGui.menuItem(I18n.translate("tangerine.ui.config"))) {
                        module.showConfigWindow()
                    }

                    ImGui.endPopup()
                }

                if (ImGui.isItemHovered()) {
                    ImGui.beginTooltip()
                    ImGui.text(I18n.translate("tangerine.module.${module.id}.description"))
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
                    ImGuiScreen.shouldClose = true
                }

                ImGui.endMenu()
            }

            drawMenuTab(
                I18n.translate("tangerine.category.movement"),
                ModuleManager.getModulesByCategory(ModuleCategory.MOVEMENT)
            )

            drawMenuTab(
                I18n.translate("tangerine.category.combat"),
                ModuleManager.getModulesByCategory(ModuleCategory.COMBAT)
            )

            drawMenuTab(
                I18n.translate("tangerine.category.visuals"),
                ModuleManager.getModulesByCategory(ModuleCategory.VISUALS)
            )

            drawMenuTab(
                I18n.translate("tangerine.category.player"),
                ModuleManager.getModulesByCategory(ModuleCategory.PLAYER)
            )

            drawMenuTab(
                I18n.translate("tangerine.category.misc"),
                ModuleManager.getModulesByCategory(ModuleCategory.MISC)
            )

            ImGui.endMainMenuBar()
        }

        if (openKeybindMenu) ImGuiManager.opened.openHotkeyMenu()
        ImGuiManager.opened.drawHotkeyMenu()
    }
}
