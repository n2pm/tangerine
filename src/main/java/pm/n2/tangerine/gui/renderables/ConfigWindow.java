package pm.n2.tangerine.gui.renderables;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigDouble;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.TangerineRenderable;
import pm.n2.tangerine.modules.Module;

import java.util.ArrayList;

public class ConfigWindow extends TangerineRenderable {
	private final Module module;
	private final static ArrayList<Integer> blacklistedKeys = new ArrayList<>(GLFW.GLFW_KEY_ESCAPE);

	public ConfigWindow(Module module) {
		super("ConfigWindow##" + module.name, false);
		this.module = module;
		Tangerine.IMGUI_MANAGER.addRenderable(this);
	}

	@Override
	public void draw() {
		var enabled = new ImBoolean(this.enabled);
		if (ImGui.begin(module.name, enabled)) {
			if (ImGui.checkbox("Enabled", module.enabled.getBooleanValue())) {
				module.enabled.toggle();
				if (module.enabled.getBooleanValue()) {
					module.onEnabled();
				} else {
					module.onDisabled();
				}
			}

			ImGui.text("Current keybind: " + GLFW.glfwGetKeyName(module.keybind, 0));
			ImGui.sameLine();
			if (ImGui.button("Assign keybind")) {
				ImGui.openPopup("Assign keybind##" + module.name);
			}

			var configOptions = module.getConfigOptions();
			if (configOptions != null) {
				for (var config : configOptions) {
					if (config instanceof ConfigBoolean configBoolean) {
						if (ImGui.checkbox(configBoolean.getName(), configBoolean.getBooleanValue())) {
							configBoolean.toggle();
						}
					}

					if (config instanceof ConfigDouble configDouble) {
						var doubleValue = configDouble.getDoubleValue();
						var doubleValueArr = new float[]{(float) doubleValue};
						if (ImGui.dragFloat(configDouble.getName(), doubleValueArr, 0.1f, (float) configDouble.getMinValue(), (float) configDouble.getMaxValue())) {
							configDouble.setDoubleValue(doubleValueArr[0]);
						}
					}
				}
			}

			var flags = ImGuiWindowFlags.NoCollapse
					| ImGuiWindowFlags.NoResize
					| ImGuiWindowFlags.NoMove
					| ImGuiWindowFlags.AlwaysAutoResize;

			if (ImGui.beginPopupModal("Assign keybind##" + module.name, new ImBoolean(true), flags)) {
				ImGui.text("Press a key to assign it to this module.");

				for (int i = 0; i < 512; i++) {
					if (blacklistedKeys.contains(i)) continue;
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
