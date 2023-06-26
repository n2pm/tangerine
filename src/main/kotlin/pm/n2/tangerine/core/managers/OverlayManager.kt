package pm.n2.tangerine.core.managers

import com.adryd.cauldron.api.render.helper.OverlayRenderManager
import com.adryd.cauldron.api.render.helper.OverlayRendererBase
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.render.OverlayBlockESP
import pm.n2.tangerine.render.OverlayStorageESP
import pm.n2.tangerine.render.OverlayTracers

object OverlayManager : Manager<OverlayRendererBase>() {
    override val items = listOf(
        OverlayBlockESP,
        OverlayStorageESP,
        OverlayTracers
    )

    fun init() {
        items.forEach { OverlayRenderManager.addRenderer(it) }
    }
}
