package pm.n2.tangerine.commands.arguments.pos

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

interface CauldronPosArgument {
    fun toAbsolutePos(source: CauldronClientCommandSource): Vec3d
    fun toAbsoluteRotation(source: CauldronClientCommandSource): Vec2f

    val isXRelative: Boolean
    val isYRelative: Boolean
    val isZRelative: Boolean
}
