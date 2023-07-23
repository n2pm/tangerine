package pm.n2.tangerine.modules.movement

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.math.Vec3d
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.config.DoubleConfigOption
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.managers.PaperClipManager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object FlightModule : Module("flight", ModuleCategory.MOVEMENT) {
    val flyKickBypass = BooleanConfigOption(id, "fly_kick", true)
    val flyScrollSpeed = BooleanConfigOption(id, "scroll_speed", false)
    val flyFriction = BooleanConfigOption(id, "friction", false)
    val flySpeed = DoubleConfigOption(id, "speed", 1.0, 0.1, 20.0)
    override val configOptions = listOf(flyKickBypass, flyScrollSpeed, flyFriction, flySpeed)

    private var fallingTicks = 0
    private var lastPos: Vec3d? = null

    @EventHandler
    fun onPreTick(event: TangerineEvent.PreTick) {
        val mc = Tangerine.mc
        if (mc.player != null && enabled.value) {
            val abilities = mc.player!!.abilities
            val canNormallyFly = mc.player!!.isSpectator || mc.player!!.isCreative
            if (abilities.flying && !canNormallyFly) lastPos = mc.player!!.pos
        }
    }

    @EventHandler
    fun onPostTick(event: TangerineEvent.PostTick) {
        val mc = Tangerine.mc
        if (mc.player != null && enabled.value) {
            val abilities = mc.player!!.abilities
            abilities.flySpeed = flySpeed.value.toFloat() / 20

            val canNormallyFly = mc.player!!.isSpectator || mc.player!!.isCreative
            if (!canNormallyFly) abilities.allowFlying = true

            if ((abilities.flying && !canNormallyFly) || PaperClipManager.isRunning) {
                fallingTicks++

                if (fallingTicks >= 20) {
                    var shouldAntiKick = flyKickBypass.value

                    // are we descending?
                    if (mc.player!!.isSneaking) {
                        if (lastPos != null && mc.player!!.y < lastPos!!.getY()) shouldAntiKick = false
                    }

                    if (shouldAntiKick) {
                        val packet = if (mc.player!!.hasVehicle()) {
                            PaperClipManager.buildVehicleMove(
                                mc.player!!.vehicle!!.x,
                                mc.player!!.vehicle!!.y - 0.03126,
                                mc.player!!.vehicle!!.z,
                                mc.player!!.vehicle!!.yaw,
                                mc.player!!.vehicle!!.pitch
                            )
                        } else {
                            PositionAndOnGround(mc.player!!.x, mc.player!!.y - 0.03126, mc.player!!.z, true)
                        }
                        mc.player!!.networkHandler.sendPacket(packet)
                    }

                    fallingTicks = 0
                }
            }
        }
    }

    override fun onDisabled() {
        val mc = Tangerine.mc
        if (mc.player != null && mc.player!!.isSpectator && mc.player!!.isCreative) {
            mc.player!!.abilities.allowFlying = false
            mc.player!!.abilities.flying = false
        }
    }
}
