package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

class MenuBar : TangerineRenderable("MenuBar") {
    private fun drawMenuTab(name: String, modules: List<Module>) {
        if (ImGui.beginMenu(name)) {
            for (module in modules) {
                if (ImGui.menuItem(module.name, "", module.enabled.booleanValue)) {
                    module.enabled.toggle()
                    if (module.enabled.booleanValue) {
                        module.onEnabled()
                    } else {
                        module.onDisabled()
                    }
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
                val demoWindow = manager.get("DemoWindow")!!
                val aboutWindow = manager.get("AboutWindow")!!

                if (ImGui.menuItem("About Tangerine", "", aboutWindow.enabled)) {
                    aboutWindow.enabled = !aboutWindow.enabled
                }

                if (ImGui.menuItem("Open ImGui demo", "", demoWindow.enabled)) {
                    demoWindow.enabled = !demoWindow.enabled
                }

                if (ImGui.menuItem("Close menu bar")) {
                    Tangerine.imguiScreen.shouldClose = true
                }

                ImGui.endMenu()
            }

            drawMenuTab("Movement", Tangerine.moduleManager.getModulesByCategory(ModuleCategory.MOVEMENT))
            drawMenuTab("Combat", Tangerine.moduleManager.getModulesByCategory(ModuleCategory.COMBAT))
            drawMenuTab("Visuals", Tangerine.moduleManager.getModulesByCategory(ModuleCategory.VISUALS))
            drawMenuTab("Player", Tangerine.moduleManager.getModulesByCategory(ModuleCategory.PLAYER))
            drawMenuTab("Misc", Tangerine.moduleManager.getModulesByCategory(ModuleCategory.MISC))

            ImGui.endMainMenuBar()
        }
    }
}
