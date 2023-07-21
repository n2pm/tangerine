package pm.n2.tangerine.managers

import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.Manager

object ManagerManager : Manager {
    private val managers = listOf(
        ClipManager,
        CommandManager,
        EventManager,
        ImGuiManager,
        KeyboardManager,
        ModuleManager,
        OverlayManager,
        ExtensionManager
    )

    override fun init() {
        managers.forEach {
            Tangerine.eventManager.registerClass(it)
            it.init()
        }
    }
}
