package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import pm.n2.tangerine.Module;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.TangerineRenderable;
import pm.n2.tangerine.ModuleCategory;

import java.util.List;

public class MenuBar extends TangerineRenderable {
	public MenuBar() {
		super("MenuBar");
	}

	private void drawMenuTab(String name, List<Module> modules) {
		if (ImGui.beginMenu(name)) {
			for (var module : modules) {
				if (ImGui.menuItem(module.name, "", module.enabled)) {
					module.enabled = !module.enabled;
					if (module.enabled) {
						module.onEnabled();
					} else {
						module.onDisabled();
					}
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

			if (ImGui.beginMenu("Visuals")) {
				ImGui.menuItem("ESP");
				ImGui.menuItem("Fullbright");
				ImGui.menuItem("Chams");
				ImGui.menuItem("X-Ray");
				ImGui.menuItem("Tracers");
				ImGui.menuItem("No weather");

				ImGui.endMenu();
			}

			drawMenuTab("Player", Tangerine.MODULE_MANAGER.getModulesByCategory(ModuleCategory.PLAYER));

			if (ImGui.beginMenu("Automation")) {
				ImGui.menuItem("Auto armor");
				ImGui.menuItem("Auto eat");
				ImGui.menuItem("Auto mine");
				ImGui.menuItem("Auto tool");
				ImGui.menuItem("Auto walk");
				ImGui.menuItem("Auto sprint");

				ImGui.endMenu();
			}

			ImGui.endMainMenuBar();
		}
	}
}
