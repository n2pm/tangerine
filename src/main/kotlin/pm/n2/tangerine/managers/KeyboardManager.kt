package pm.n2.tangerine.managers

import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.core.TangerineEvent

object KeyboardManager : Manager {
    private val pressedKeys = ArrayList<Int>()

    fun isKeyPressed(key: Int): Boolean {
        return pressedKeys.contains(key)
    }

    fun registerKeyPress(key: Int): Boolean {
        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key)
            return Tangerine.eventManager.dispatch(TangerineEvent.KeyPress(key)).filterIsInstance<Boolean>().any { it }
        }

        return false
    }

    fun registerKeyRelease(key: Int) {
        if (pressedKeys.contains(key)) pressedKeys.remove(key)
    }
}
