package pm.n2.tangerine.gui.renderables;

import gay.eviee.imguiquilt.interfaces.Renderable;
import gay.eviee.imguiquilt.interfaces.Theme;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.TangerineRenderable;

public class MenuBar extends TangerineRenderable {
	public MenuBar() {
		super("MenuBar");
	}

	@Override
	public void draw() {
		if (ImGui.beginMainMenuBar()) {
			if (ImGui.beginMenu("Movement")) {
				ImGui.menuItem("Flight");
				ImGui.menuItem("No fall damage");
				ImGui.menuItem("Walk on liquids");
				ImGui.menuItem("Speedhack");
				ImGui.menuItem("No slowdown");

				ImGui.endMenu();
			}

			if (ImGui.beginMenu("Visuals")) {
				ImGui.menuItem("ESP");
				ImGui.menuItem("Fullbright");
				ImGui.menuItem("Chams");
				ImGui.menuItem("Wireframe");
				ImGui.menuItem("X-Ray");
				ImGui.menuItem("Tracers");
				ImGui.menuItem("No weather");

				ImGui.endMenu();
			}

			if (ImGui.beginMenu("Automation")) {
				ImGui.menuItem("Auto armor");
				ImGui.menuItem("Auto eat");
				ImGui.menuItem("Auto mine");
				ImGui.menuItem("Auto tool");
				ImGui.menuItem("Auto walk");
				ImGui.menuItem("Auto sprint");

				ImGui.endMenu();
			}

			if (ImGui.beginMenu("Misc")) {
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

			ImGui.endMainMenuBar();
		}
	}
}
