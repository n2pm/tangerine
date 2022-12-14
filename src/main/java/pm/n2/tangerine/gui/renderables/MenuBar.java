package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.TangerineRenderable;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

import java.util.List;

public class MenuBar extends TangerineRenderable {
	public MenuBar() {
		super("MenuBar");
	}

	private void drawMenuTab(String name, List<Module> modules) {
		if (ImGui.beginMenu(name)) {
			for (var module : modules) {
				if (ImGui.menuItem(module.name, "", module.enabled.getBooleanValue())) {
					module.enabled.toggle();
					if (module.enabled.getBooleanValue()) {
						module.onEnabled();
					} else {
						module.onDisabled();
					}
				}

				if (ImGui.beginPopupContextItem()) {
					if (ImGui.menuItem("Config")) {
						module.showConfigWindow();
					}

					ImGui.endPopup();
				}

				if (ImGui.isItemHovered()) {
					ImGui.beginTooltip();
					ImGui.text(module.description);
					ImGui.endTooltip();
				}
			}

			ImGui.endMenu();
		}
	}
	@Override
	public void draw() {
		if (ImGui.beginMainMenuBar()) {
			if (ImGui.beginMenu("Tangerine")) {
				var demoWindow = manager.get("DemoWindow");
				var aboutWindow = manager.get("AboutWindow");

				if (ImGui.menuItem("About Tangerine", "", aboutWindow.enabled)) {
					aboutWindow.enabled = !aboutWindow.enabled;
				}

				if (ImGui.menuItem("Open ImGui demo", "", demoWindow.enabled)) {
					demoWindow.enabled = !demoWindow.enabled;
				}

				if (ImGui.menuItem("Close menu bar")) {
					Tangerine.IMGUI_SCREEN.shouldClose = true;
				}

				ImGui.endMenu();
			}

			drawMenuTab("Movement", Tangerine.MODULE_MANAGER.getModulesByCategory(ModuleCategory.MOVEMENT));
			drawMenuTab("Combat", Tangerine.MODULE_MANAGER.getModulesByCategory(ModuleCategory.COMBAT));
			drawMenuTab("Visuals", Tangerine.MODULE_MANAGER.getModulesByCategory(ModuleCategory.VISUALS));
			drawMenuTab("Player", Tangerine.MODULE_MANAGER.getModulesByCategory(ModuleCategory.PLAYER));
			drawMenuTab("Automation", Tangerine.MODULE_MANAGER.getModulesByCategory(ModuleCategory.AUTOMATION));
			drawMenuTab("Misc", Tangerine.MODULE_MANAGER.getModulesByCategory(ModuleCategory.MISC));

			ImGui.endMainMenuBar();
		}
	}
}
