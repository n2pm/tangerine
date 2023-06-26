package pm.n2.tangerine.core

sealed class TangerineEvent {
    object PreTick : TangerineEvent()
    object PostTick : TangerineEvent()
    object ModuleEnabled : TangerineEvent()
    object ModuleDisabled : TangerineEvent()

    class KeyPress(val key: Int) : TangerineEvent()
}
