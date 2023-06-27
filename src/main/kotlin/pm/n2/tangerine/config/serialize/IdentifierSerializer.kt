package pm.n2.tangerine.config.serialize

import com.google.gson.*
import net.minecraft.util.Identifier
import java.lang.reflect.Type

class IdentifierSerializer : JsonSerializer<Identifier>, JsonDeserializer<Identifier> {
    override fun serialize(src: Identifier?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Identifier {
        return Identifier(json?.asString)
    }
}
