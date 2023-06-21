package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.extension.implot.ImPlot
import imgui.type.ImBoolean
import pm.n2.tangerine.gui.TangerineRenderable

class DemoWindow : TangerineRenderable("DemoWindow", false) {
    private var drawImGuiDemo = ImBoolean(false)
    private var drawImPlotDemo = ImBoolean(false)

    override fun draw() {
        val enabled = ImBoolean(this.enabled)

        if (ImGui.begin("Demo windows", enabled)) {
            ImGui.checkbox("Draw ImGui demo", drawImGuiDemo)
            ImGui.checkbox("Draw ImPlot demo", drawImPlotDemo)
        }

        ImGui.end()
        this.enabled = enabled.get()

        ImGui.showDemoWindow(drawImGuiDemo)
        ImPlot.showDemoWindow(drawImPlotDemo)
    }
}
