package pm.n2.tangerine.commands.arguments.pos

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.mojang.brigadier.StringReader
import net.minecraft.command.argument.CoordinateArgument
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

class CauldronLookingPosArgument(
    val x: Double,
    val y: Double,
    val z: Double
) : CauldronPosArgument {
    override fun toAbsolutePos(source: CauldronClientCommandSource): Vec3d {
        val vec2f = source.player.rotationClient
        val vec3d = EntityAnchor.FEET.positionAt(source.player)

        val f = MathHelper.cos((vec2f.y + 90.0f) * (Math.PI.toFloat() / 180))
        val g = MathHelper.sin((vec2f.y + 90.0f) * (Math.PI.toFloat() / 180))
        val h = MathHelper.cos(-vec2f.x * (Math.PI.toFloat() / 180))
        val i = MathHelper.sin(-vec2f.x * (Math.PI.toFloat() / 180))
        val j = MathHelper.cos((-vec2f.x + 90.0f) * (Math.PI.toFloat() / 180))
        val k = MathHelper.sin((-vec2f.x + 90.0f) * (Math.PI.toFloat() / 180))

        val vec3d2 = Vec3d((f * h).toDouble(), i.toDouble(), (g * h).toDouble())
        val vec3d3 = Vec3d((f * j).toDouble(), k.toDouble(), (g * j).toDouble())
        val vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0)

        val d = vec3d2.x * z + vec3d3.x * y + vec3d4.x * x
        val e = vec3d2.y * z + vec3d3.y * y + vec3d4.y * x
        val l = vec3d2.z * z + vec3d3.z * y + vec3d4.z * x

        return Vec3d(vec3d.x + d, vec3d.y + e, vec3d.z + l)
    }

    override fun toAbsoluteRotation(source: CauldronClientCommandSource): Vec2f = Vec2f.ZERO

    override val isXRelative = true
    override val isYRelative = true
    override val isZRelative = true

    companion object {
        fun parse(reader: StringReader): CauldronLookingPosArgument {
            val i = reader.cursor
            val d = readCoordinate(reader, i)
            if (!reader.canRead() || reader.peek() != ' ') {
                reader.cursor = i
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader)
            }
            reader.skip()
            val e = readCoordinate(reader, i)
            if (!reader.canRead() || reader.peek() != ' ') {
                reader.cursor = i
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader)
            }
            reader.skip()
            val f = readCoordinate(reader, i)
            return CauldronLookingPosArgument(d, e, f)
        }

        fun readCoordinate(reader: StringReader, startingCursorPos: Int): Double {
            if (!reader.canRead()) {
                throw CoordinateArgument.MISSING_COORDINATE.createWithContext(reader)
            }
            if (reader.peek() != '^') {
                reader.cursor = startingCursorPos
                throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(reader)
            }
            reader.skip()
            return if (reader.canRead() && reader.peek() != ' ') reader.readDouble() else 0.0
        }
    }
}

