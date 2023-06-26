package pm.n2.tangerine.gui.renderables

import imgui.ImGui
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
                if (ImGui.menuItem(module.name, "", module.enabled.booleanValue)) {
                    ModuleManager.toggle(module)
                }

                if (ImGui.beginPopupContextItem()) {
                    if (ImGui.menuItem("Config")) {
                        module.showConfigWindow()
                    }
                    ImGui.endPopup()
                }

                if (ImGui.isItemHovered()) {
                    ImGui.beginTooltip()
                    ImGui.text(module.description)
                    ImGui.endTooltip()
                }
            }
            ImGui.endMenu()
        }
    }

    override fun draw() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Tangerine")) {
                val demoWindow = ImGuiManager.get("DemoWindow")!!
                val aboutWindow = ImGuiManager.get("AboutWindow")!!

                if (ImGui.menuItem("About Tangerine", "", aboutWindow.enabled)) {
                    aboutWindow.enabled = !aboutWindow.enabled
                }

                if (ImGui.menuItem("Open ImGui demo", "", demoWindow.enabled)) {
                    demoWindow.enabled = !demoWindow.enabled
                }

                if (ImGui.menuItem("Close menu bar")) {
                    ImGuiScreen.shouldClose = true
                }

                ImGui.endMenu()
            }

            drawMenuTab("Movement", ModuleManager.getModulesByCategory(ModuleCategory.MOVEMENT))
            drawMenuTab("Combat", ModuleManager.getModulesByCategory(ModuleCategory.COMBAT))
            drawMenuTab("Visuals", ModuleManager.getModulesByCategory(ModuleCategory.VISUALS))
            drawMenuTab("Player", ModuleManager.getModulesByCategory(ModuleCategory.PLAYER))
            drawMenuTab("Misc", ModuleManager.getModulesByCategory(ModuleCategory.MISC))

            ImGui.endMainMenuBar()
        }
    }
}
