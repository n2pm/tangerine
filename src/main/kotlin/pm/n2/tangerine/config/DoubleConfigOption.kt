package pm.n2.tangerine.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class DoubleConfigOption(
    override val group: String,
    override val name: String,
    override var value: Double,
    val min: Double,
    val max: Double
) : ConfigOption<Double>(group, name, value) {
    fun set(value: Double) {
        this.value = value
        cap()
    }

    private fun cap() {
        if (value < min) value = min
        if (value > max) value = max
    }

    override fun parse(json: JsonElement) {
        value = json.asDouble
        cap()
    }

    override fun write(): JsonElement = JsonPrimitive(value)
}
