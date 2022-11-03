package pm.n2.tangerine.gui.renderables;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigOptionBase;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.TangerineRenderable;
import pm.n2.tangerine.modules.Module;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ConfigWindow extends TangerineRenderable {
	private Module module;
	private HashMap<String, ConfigOptionBase> configOptions = new HashMap<>();

	public ConfigWindow(Module module) {
		super("ConfigWindow##" + module.name, false);
		this.module = module;
		Tangerine.IMGUI_MANAGER.addRenderable(this);

		// jules(2022-11-02): i did not write this code. cyn did. please do not credit the magic to me
		try {
			Class<?> moduleClass = Class.forName(module.getClass().getName(), true, ClassLoader.getSystemClassLoader());
			for (Field field : moduleClass.getFields()) {
				Tangerine.LOGGER.info("[{}] {}: {}", module.getClass().getName(), field.getType().getName(), field.getType().getSuperclass());
				if (field.getType().getSuperclass() == ConfigOptionBase.class) {
					configOptions.put(field.getName(), (ConfigOptionBase) field.get(module));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

			configOptions.forEach((name, config) -> {
				if (config instanceof ConfigBoolean configBoolean) {
					if (ImGui.checkbox(configBoolean.getKey(), configBoolean.getBooleanValue())) {
						configBoolean.toggle();
					}
				}
			});


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
