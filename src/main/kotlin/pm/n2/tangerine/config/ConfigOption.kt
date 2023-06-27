package pm.n2.tangerine.config

import com.google.gson.JsonElement

abstract class ConfigOption<T>(open val group: String, open val name: String, open var value: T) {
    var keybind: ConfigKeybind? = null

    open fun onKeybind() {}
    abstract fun parse(json: JsonElement)
    abstract fun write(): JsonElement
}
