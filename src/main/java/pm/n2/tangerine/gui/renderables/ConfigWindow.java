package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import imgui.type.ImBoolean;
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
			ImGui.text("nuts");
		}

		ImGui.end();

		this.enabled = enabled.get();
	}
}
