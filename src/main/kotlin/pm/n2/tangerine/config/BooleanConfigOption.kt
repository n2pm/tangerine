package pm.n2.tangerine.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import pm.n2.tangerine.Tangerine

class BooleanConfigOption(override val group: String, override val name: String, override var value: Boolean) :
    ConfigOption<Boolean>(group, name, value) {
    fun toggle() = set(!value)
    override fun onKeybind() = toggle()

    override fun parse(json: JsonElement) {
        value = json.asBoolean
    }

    override fun write(): JsonElement = JsonPrimitive(value)

}
