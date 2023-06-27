package pm.n2.tangerine.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.quiltmc.loader.api.QuiltLoader

object TangerineConfig {
    private val trackedOptions = mutableListOf<ConfigOption<*>>()
    private val configFile = QuiltLoader.getConfigDir().resolve("tangerine.json").toFile()
    private val GSON = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(ConfigKeybind::class.java, ConfigKeybind.ConfigKeybindSerializer())
        .create()

    lateinit var root: JsonObject

    fun addConfigOptions(options: List<ConfigOption<*>>) = trackedOptions.addAll(options)

    fun read() {
        if (!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText("{}")
        }

        root = GSON.fromJson(configFile.readText(), JsonObject::class.java)

        for (option in trackedOptions) {
            val group = root.get(option.group) ?: continue
            val entry = group.asJsonObject.get(option.name) ?: continue
            val entryObj = entry.asJsonObject ?: continue

            option.parse(entryObj.get("value"))

            val keybind = entryObj.get("keybind")
            if (keybind != null && keybind.isJsonObject) {
                option.keybind = GSON.fromJson(keybind, ConfigKeybind::class.java)
            }
        }
    }

    fun write() {
        val groups = trackedOptions.groupBy { it.group }

        val newObject = JsonObject()
        for (group in groups) {
            val groupName = group.key
            val groupObject = JsonObject()

            for (option in group.value) {
                val name = option.name

                val valueObject = JsonObject()
                valueObject.add("value", option.write())
                valueObject.add(
                    "keybind", if (option.keybind != null)
                        GSON.toJsonTree(option.keybind, ConfigKeybind::class.java)
                    else null
                )

                groupObject.add(name, valueObject)
            }

            newObject.add(groupName, groupObject)
        }

        val str = GSON.toJson(newObject)
        configFile.writeText(str)
    }
}
