package pm.n2.tangerine.config

import org.lwjgl.glfw.GLFW
import pm.n2.tangerine.managers.KeyboardManager

class ConfigKeybind(var key: Int, var shift: Boolean = false, var ctrl: Boolean = false, var alt: Boolean = false) {
    fun isPressed(): Boolean {
        val pressed = KeyboardManager.isKeyPressed(key)

        val shiftPressed = KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)
                || KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT)
        val ctrlPressed = KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
                || KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)
        val altPressed = KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT)
                || KeyboardManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT)

        if (shift && !shiftPressed) return false
        if (ctrl && !ctrlPressed) return false
        if (alt && !altPressed) return false

        return pressed
    }

    fun isJustPressed(key: Int): Boolean = isPressed() && this.key == key

    override fun toString(): String {
        val ctrl = if (ctrl) "Ctrl+" else ""
        val shift = if (shift) "Shift+" else ""
        val alt = if (alt) "Alt+" else ""

        return "$ctrl$shift$alt${GLFW.glfwGetKeyName(key, 0)}"
    }
}
