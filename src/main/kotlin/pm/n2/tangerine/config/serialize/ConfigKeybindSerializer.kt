package pm.n2.tangerine.config.serialize

import com.google.gson.*
import pm.n2.tangerine.config.ConfigKeybind
import java.lang.reflect.Type

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
