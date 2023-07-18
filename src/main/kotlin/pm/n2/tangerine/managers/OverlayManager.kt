package pm.n2.tangerine.managers

import com.adryd.cauldron.api.render.helper.OverlayRenderManager
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.render.OverlayBlockESP
import pm.n2.tangerine.render.OverlayStorageESP
import pm.n2.tangerine.render.OverlayTracers

object OverlayManager : Manager {
    private val overlays = listOf(
        OverlayBlockESP,
        OverlayStorageESP,
        OverlayTracers
    )

    override fun init() {
        overlays.forEach { OverlayRenderManager.addRenderer(it) }
    }
}
