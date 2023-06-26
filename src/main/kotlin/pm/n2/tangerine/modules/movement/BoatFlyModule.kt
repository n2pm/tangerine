package pm.n2.tangerine.modules.movement

import com.adryd.cauldron.api.config.ConfigBoolean
import com.adryd.cauldron.api.config.ConfigDouble
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.mixin.BoatEntityAccessor
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object BoatFlyModule : Module("boatfly", "Boat fly", "magic carpet lookin ass", ModuleCategory.MOVEMENT) {
    val flySpeed = ConfigDouble("boatfly.speed", 1.0, 0.1, 5.0)
    val landSpeed = ConfigBoolean("boatfly.landspeed", false)
    override val configOptions = listOf(flySpeed, landSpeed)

    @EventHandler
    fun onPostTick(event: TangerineEvent.PostTick) {
        val mc = Tangerine.mc
        if (mc.player != null && this.enabled.booleanValue) {
            val player = mc.player!!

            if (player.vehicle is BoatEntity) {
                val vehicle = (player.vehicle as BoatEntity)

                val yaw = vehicle.yaw * (Math.PI / 180.0).toFloat()
                val speed = flySpeed.doubleValue
                var y = if (player.input.jumping) speed else 0.0

                val location = (vehicle as BoatEntityAccessor).location
                if (location == BoatEntity.Location.IN_AIR || landSpeed.booleanValue && location == BoatEntity.Location.ON_LAND) {
                    var forward = 0
                    if (player.input.pressingForward) {
                        forward = 1
                    } else if (player.input.pressingBack) {
                        forward = -1
                    }

                    if (mc.options.sprintKey.isPressed && location == BoatEntity.Location.IN_AIR && !player.input.jumping) {
                        y = -speed
                    }

                    val forwardDir = Vec3d(-MathHelper.sin(yaw).toDouble(), 0.0, MathHelper.cos(yaw).toDouble())
                    val forwardSpeed = forwardDir.multiply(speed).multiply(forward.toDouble())
                    vehicle.velocity = Vec3d(forwardSpeed.x, y, forwardSpeed.z)
                } else {
                    val vel = vehicle.velocity
                    vehicle.velocity = Vec3d(vel.x, y, vel.z)
                }
            }
        }
    }
}
