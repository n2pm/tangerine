package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import pm.n2.tangerine.gui.TangerineRenderable;

public class DemoWindow extends TangerineRenderable {
	public DemoWindow() {
		super("DemoWindow", false);
	}

	@Override
	public void draw() {
		ImGui.showDemoWindow();
	}
}
