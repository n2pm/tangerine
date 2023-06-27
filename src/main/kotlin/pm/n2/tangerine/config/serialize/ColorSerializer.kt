package pm.n2.tangerine.config.serialize

import com.google.gson.*
import java.awt.Color
import java.lang.reflect.Type

class ColorSerializer : JsonSerializer<Color>, JsonDeserializer<Color> {
    override fun serialize(src: Color?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val obj = JsonObject()

        obj.addProperty("r", src?.red ?: 255)
        obj.addProperty("g", src?.green ?: 255)
        obj.addProperty("b", src?.blue ?: 255)
        obj.addProperty("a", src?.alpha ?: 255)

        return obj
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Color {
        val obj = json?.asJsonObject ?: return Color(255, 255, 255, 255)

        val r = obj.get("r")?.asInt ?: 255
        val g = obj.get("g")?.asInt ?: 255
        val b = obj.get("b")?.asInt ?: 255
        val a = obj.get("a")?.asInt ?: 255

        return Color(r, g, b, a)
    }
}
