package pm.n2.tangerine

import pm.n2.tangerine.core.TangerineEvent

object KeyboardManager {
    private val pressedKeys = ArrayList<Int>()

    fun isKeyPressed(key: Int): Boolean {
        return pressedKeys.contains(key)
    }

    fun registerKeyPress(key: Int) {
        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key)
            Tangerine.eventManager.dispatch(TangerineEvent.KeyPress(key))
        }
    }

    fun registerKeyRelease(key: Int) {
        if (pressedKeys.contains(key)) pressedKeys.remove(key)
    }
}
