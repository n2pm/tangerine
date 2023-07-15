package pm.n2.tangerine.core

sealed class TangerineEvent {
    data object PreTick : TangerineEvent()
    data object PostTick : TangerineEvent()
    data object ModuleEnabled : TangerineEvent()
    data object ModuleDisabled : TangerineEvent()
    data object ImGuiDraw : TangerineEvent()

    data class KeyPress(val key: Int) : TangerineEvent()
}
