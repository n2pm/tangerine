package pm.n2.tangerine.config

import com.google.gson.JsonElement

class ListConfigOption<T>(
    override val group: String,
    override val name: String,
    val clazz: Class<T>,
    override var value: MutableList<T>
) : ConfigOption<MutableList<T>>(group, name, value) {
    override fun parse(json: JsonElement) {
        value = json.asJsonArray.map {
            TangerineConfig.GSON.fromJson(it, clazz)
        }.toMutableList()
    }
}
