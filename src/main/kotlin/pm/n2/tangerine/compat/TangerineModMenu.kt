package pm.n2.tangerine.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import pm.n2.tangerine.gui.TangerineScreen

class TangerineModMenu : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { TangerineScreen }
    }
}
