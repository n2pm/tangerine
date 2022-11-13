package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import imgui.type.ImBoolean;
import pm.n2.tangerine.gui.TangerineRenderable;

public class DemoWindow extends TangerineRenderable {
	private ImBoolean drawImGuiDemo = new ImBoolean(false);
	private ImBoolean drawImPlotDemo = new ImBoolean(false);

	public DemoWindow() {
		super("DemoWindow", false);
	}

	@Override
	public void draw() {
		var enabled = new ImBoolean(this.enabled);
		if (ImGui.begin("Demo windows", enabled)) {
			ImGui.checkbox("Draw ImGui demo", drawImGuiDemo);
			ImGui.checkbox("Draw ImPlot demo", drawImPlotDemo);
		}

		ImGui.end();
		this.enabled = enabled.get();

		if (drawImGuiDemo.get()) ImGui.showDemoWindow();
		if (drawImPlotDemo.get()) ImPlot.showDemoWindow(drawImPlotDemo);
	}
}
