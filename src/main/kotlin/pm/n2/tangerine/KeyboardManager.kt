package pm.n2.tangerine

import org.quiltmc.qsl.base.api.event.Event
import pm.n2.tangerine.KeyboardManager.KeyPress

class KeyboardManager {
    val keyPress = Event.create(KeyPress::class.java) { listeners -> KeyPress { key -> listeners.forEach { it.onKeyPress(key) } } }
    private val pressedKeys = ArrayList<Int>()

    fun isKeyPressed(key: Int): Boolean {
        return pressedKeys.contains(key)
    }

    fun registerKeyPress(key: Int) {
        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key)
            keyPress.invoker().onKeyPress(key)
        }
    }

    fun registerKeyRelease(key: Int) {
        if (pressedKeys.contains(key)) pressedKeys.remove(key)
    }

    fun interface KeyPress {
        fun onKeyPress(key: Int)
    }
}
