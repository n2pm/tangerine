package pm.n2.tangerine.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class EnumConfigOption<T : Enum<*>>(
    override val group: String,
    override val name: String,
    override var value: T
) : ConfigOption<T>(group, name, value) {
    override fun parse(json: JsonElement) {
        value = value.javaClass.enumConstants[json.asInt]
    }

    override fun write(): JsonElement = JsonPrimitive(value.ordinal)
}
