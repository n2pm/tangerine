package pm.n2.tangerine.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class MapConfigOption<K, V>(
    override val group: String,
    override val name: String,
    override var value: MutableMap<K, V>,
    val keyClass: Class<K>,
    val valueClass: Class<V>,
) : ConfigOption<MutableMap<K, V>>(group, name, value) {
    override fun parse(json: JsonElement) {
        val obj = json.asJsonObject ?: return
        value = obj.asMap().map {
            // this fucking sucks
            val keyStr = JsonPrimitive(it.key)
            val key = TangerineConfig.GSON.fromJson(keyStr, keyClass)
            val value = TangerineConfig.GSON.fromJson(it.value, valueClass)
            key to value
        }.toMap().toMutableMap()
    }
}

