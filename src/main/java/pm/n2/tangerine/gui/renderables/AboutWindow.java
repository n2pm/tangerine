package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.util.Util;
import pm.n2.tangerine.gui.TangerineRenderable;

public class AboutWindow extends TangerineRenderable {
	public AboutWindow() {
		super("AboutWindow", false);
	}

	@Override
	public void draw() {
		var enabled = new ImBoolean(this.enabled);

		if (ImGui.begin("About Tangerine", enabled)) {
			ImGui.textUnformatted("Tangerine, a NotNet project");
			ImGui.textUnformatted("Powered by Cauldron and imgui-quilt");
			ImGui.textUnformatted("(c) NotNite and adryd, 2022");

			if (ImGui.button("Open source code")) {
				Util.getOperatingSystem().open("https://github.com/n2pm/tangerine");
			}

			if (ImGui.button("Crash game")) {
				throw new RuntimeException("She strogan my beef til im off");
			}
		}

		ImGui.end();

		this.enabled = enabled.get();
	}
}
