package pm.n2.tangerine.config

import com.google.gson.*
import org.lwjgl.glfw.GLFW
import pm.n2.tangerine.KeyboardManager
import java.lang.reflect.Type

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

    class ConfigKeybindSerializer : JsonSerializer<ConfigKeybind>, JsonDeserializer<ConfigKeybind> {
        override fun serialize(
            src: ConfigKeybind?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            val obj = JsonObject()

            obj.addProperty("key", src?.key ?: -1)
            obj.addProperty("shift", src?.shift ?: false)
            obj.addProperty("ctrl", src?.ctrl ?: false)
            obj.addProperty("alt", src?.alt ?: false)

            return obj
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ConfigKeybind {
            if (json !is JsonObject) throw JsonParseException("Expected ConfigKeybind to be an object")

            val key = json.get("key")?.asInt ?: -1
            val shift = json.get("shift")?.asBoolean ?: false
            val ctrl = json.get("ctrl")?.asBoolean ?: false
            val alt = json.get("alt")?.asBoolean ?: false

            return ConfigKeybind(key, shift, ctrl, alt)
        }
    }
}
