package pm.n2.tangerine.modules.misc

import pm.n2.tangerine.gui.ImGuiManager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object UnifontModule : Module("unifont", "Unifont", "Makes all text use the Unifont font", ModuleCategory.MISC) {
    override fun onEnabled() {
        ImGuiManager.setFont(ImGuiManager.fontUnifont)
    }

    override fun onDisabled() {
        ImGuiManager.setFont(ImGuiManager.fontDefault)
    }
}
