package pm.n2.tangerine.gui.renderables;

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

			if (ImGui.beginMenu("Movement")) {
				if (ImGui.menuItem("Flight", "", Tangerine.MODULE_STATE.flight)) {
					Tangerine.MODULE_STATE.flight = !Tangerine.MODULE_STATE.flight;

					if (!Tangerine.MODULE_STATE.flight) {
						var mc = MinecraftClient.getInstance();
						if (mc.player != null) {
							if (!mc.player.isSpectator() && !mc.player.isCreative()) {
								mc.player.getAbilities().allowFlying = false;
								mc.player.getAbilities().flying = false;
							}
						}
					}
				}

				if (ImGui.menuItem("No fall damage", "", Tangerine.MODULE_STATE.noFall)) {
					Tangerine.MODULE_STATE.noFall = !Tangerine.MODULE_STATE.noFall;
				}

				if (ImGui.menuItem("Walk on liquids", "", Tangerine.MODULE_STATE.walkOnLiquids)) {
					Tangerine.MODULE_STATE.walkOnLiquids = !Tangerine.MODULE_STATE.walkOnLiquids;
				}

				ImGui.menuItem("Speedhack");
				ImGui.menuItem("No slowdown");

				ImGui.endMenu();
			}

			if (ImGui.beginMenu("Visuals")) {
				ImGui.menuItem("ESP");
				ImGui.menuItem("Fullbright");
				ImGui.menuItem("Chams");
				ImGui.menuItem("X-Ray");
				ImGui.menuItem("Tracers");
				ImGui.menuItem("No weather");

				ImGui.endMenu();
			}

			if (ImGui.beginMenu("Player")) {
				if (ImGui.menuItem("Anti hunger", "", Tangerine.MODULE_STATE.antiHunger)) {
					Tangerine.MODULE_STATE.antiHunger = !Tangerine.MODULE_STATE.antiHunger;
				}

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

			if (ImGui.beginMenu("LO")) {
				if (ImGui.menuItem("Movement fix", "", Tangerine.MODULE_STATE.loMovementFix)) {
					Tangerine.MODULE_STATE.loMovementFix = !Tangerine.MODULE_STATE.loMovementFix;
				}

				ImGui.endMenu();
			}

			ImGui.endMainMenuBar();
		}
	}
}
