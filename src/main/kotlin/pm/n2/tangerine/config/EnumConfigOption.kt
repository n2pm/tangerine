package pm.n2.tangerine.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

@Suppress("UNCHECKED_CAST")
class EnumConfigOption<T : Enum<*>>(
    override val group: String,
    override val name: String,
    override var value: T
) : ConfigOption<T>(group, name, value) {
    override fun parse(json: JsonElement) {
        value = json.asNumber as T
    }

    override fun write(): JsonElement = JsonPrimitive(value.ordinal)
}
