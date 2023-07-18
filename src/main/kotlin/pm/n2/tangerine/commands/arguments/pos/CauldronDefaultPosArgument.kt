package pm.n2.tangerine.commands.arguments.pos

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.mojang.brigadier.StringReader
import net.minecraft.command.argument.CoordinateArgument
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d


class CauldronDefaultPosArgument(
    private val x: CoordinateArgument,
    private val y: CoordinateArgument,
    private val z: CoordinateArgument
) : CauldronPosArgument {
    override fun toAbsolutePos(source: CauldronClientCommandSource): Vec3d {
        val vec3d = source.player.pos
        return Vec3d(
            x.toAbsoluteCoordinate(vec3d.x),
            y.toAbsoluteCoordinate(vec3d.y),
            z.toAbsoluteCoordinate(vec3d.z)
        )
    }

    override fun toAbsoluteRotation(source: CauldronClientCommandSource): Vec2f {
        val vec2f = source.player.rotationClient
        return Vec2f(
            x.toAbsoluteCoordinate(vec2f.x.toDouble()).toFloat(),
            y.toAbsoluteCoordinate(vec2f.y.toDouble()).toFloat()
        )
    }

    override val isXRelative get() = x.isRelative
    override val isYRelative get() = y.isRelative
    override val isZRelative get() = z.isRelative

    override fun hashCode(): Int {
        var i = x.hashCode()
        i = 31 * i + y.hashCode()
        i = 31 * i + z.hashCode()
        return i
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CauldronDefaultPosArgument

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    companion object {
        fun parse(reader: StringReader): CauldronDefaultPosArgument {
            val i = reader.cursor
            val coordinateArgument = CoordinateArgument.parse(reader)
            if (!reader.canRead() || reader.peek() != ' ') {
                reader.cursor = i
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader)
            }
            reader.skip()
            val coordinateArgument2 = CoordinateArgument.parse(reader)
            if (!reader.canRead() || reader.peek() != ' ') {
                reader.cursor = i
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader)
            }
            reader.skip()
            val coordinateArgument3 = CoordinateArgument.parse(reader)
            return CauldronDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3)
        }

        fun parse(reader: StringReader, centerIntegers: Boolean): CauldronDefaultPosArgument {
            val i = reader.cursor
            val coordinateArgument = CoordinateArgument.parse(reader, centerIntegers)
            if (!reader.canRead() || reader.peek() != ' ') {
                reader.cursor = i
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader)
            }
            reader.skip()
            val coordinateArgument2 = CoordinateArgument.parse(reader, false)
            if (!reader.canRead() || reader.peek() != ' ') {
                reader.cursor = i
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader)
            }
            reader.skip()
            val coordinateArgument3 = CoordinateArgument.parse(reader, centerIntegers)
            return CauldronDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3)
        }
    }
}
