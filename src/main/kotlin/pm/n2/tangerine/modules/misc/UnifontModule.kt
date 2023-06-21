package pm.n2.tangerine.modules.misc

import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

class UnifontModule : Module("unifont", "Unifont", "Makes all text use the Unifont font", ModuleCategory.MISC) {
    override fun onEnabled() {
        if (Tangerine.imguiFontUnifont != null) Tangerine.imguiManager.setFont(Tangerine.imguiFontUnifont!!)
    }

    override fun onDisabled() {
        if (Tangerine.imguiFontUnifont != null) Tangerine.imguiManager.setFont(Tangerine.imguiFontDefault)
    }
}
