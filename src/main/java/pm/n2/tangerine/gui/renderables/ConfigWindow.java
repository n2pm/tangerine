package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.TangerineRenderable;
import pm.n2.tangerine.modules.Module;

public class ConfigWindow extends TangerineRenderable {
	private Module module;

	public ConfigWindow(Module module) {
		super("ConfigWindow##" + module.name, false);
		this.module = module;
		Tangerine.IMGUI_MANAGER.addRenderable(this);
	}

	@Override
	public void draw() {
		var enabled = new ImBoolean(this.enabled);
		if (ImGui.begin(module.name, enabled)) {
			ImGui.text("Current keybind: " + GLFW.glfwGetKeyName(module.keybind, 0));
			ImGui.sameLine();
			if (ImGui.button("Assign keybind")) {
				ImGui.openPopup("Assign keybind##" + module.name);
			}

			var flags = ImGuiWindowFlags.NoCollapse
					| ImGuiWindowFlags.NoResize
					| ImGuiWindowFlags.NoMove
					| ImGuiWindowFlags.AlwaysAutoResize;

			if (ImGui.beginPopupModal("Assign keybind##" + module.name, new ImBoolean(true), flags)) {
				ImGui.text("Press a key to assign it to this module.");

				for (int i = 0; i < 512; i++) {
					if (ImGui.getIO().getKeysDown(i)) {
						Tangerine.LOGGER.info("Keybind for {} set to {}", module.name, i);
						module.keybind = i;
						ImGui.closeCurrentPopup();
						break;
					}
				}

				ImGui.endPopup();
			}
		}

		ImGui.end();

		this.enabled = enabled.get();
	}
}
