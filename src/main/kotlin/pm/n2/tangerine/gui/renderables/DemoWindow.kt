package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.extension.implot.ImPlot
import imgui.type.ImBoolean
import net.minecraft.client.resource.language.I18n
import pm.n2.tangerine.gui.TangerineRenderable

object DemoWindow : TangerineRenderable("DemoWindow", false) {
    private var drawImGuiDemo = ImBoolean(false)
    private var drawImPlotDemo = ImBoolean(false)

    override fun draw() {
        val enabled = ImBoolean(this.enabled)

        if (ImGui.begin(I18n.translate("tangerine.ui.demo.name"), enabled)) {
            ImGui.checkbox(I18n.translate("tangerine.ui.demo.imgui"), drawImGuiDemo)
            ImGui.checkbox(I18n.translate("tangerine.ui.demo.implot"), drawImPlotDemo)
        }

        ImGui.end()
        this.enabled = enabled.get()

        ImGui.showDemoWindow(drawImGuiDemo)
        ImPlot.showDemoWindow(drawImPlotDemo)
    }
}
