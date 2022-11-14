package pm.n2.tangerine.modules.movement;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigDouble;
import com.adryd.cauldron.api.config.IConfigOption;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import pm.n2.tangerine.mixin.BoatEntityAccessor;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class BoatFlyModule extends Module {
	public ConfigDouble flySpeed = new ConfigDouble("boatfly_speed", "Flight Speed", 1.0, 0.1, 5.0);
	public ConfigBoolean landSpeed = new ConfigBoolean("boatfly_landspeed", "Apply flight speed to land", false);

	public BoatFlyModule() {
		super("boatfly", "Boat Fly", "magic carpet lookin ass", ModuleCategory.MOVEMENT);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null && this.enabled.getBooleanValue()) {
			var player = mc.player;
			if (player.getVehicle() instanceof BoatEntity vehicle) {
				var yaw = vehicle.getYaw() * (float) (Math.PI / 180.0);
				var speed = flySpeed.getDoubleValue();

				var y = player.input.jumping ? speed : 0;

				var location = ((BoatEntityAccessor) vehicle).getLocation();
				if (location == BoatEntity.Location.IN_AIR || (landSpeed.getBooleanValue() && location == BoatEntity.Location.ON_LAND)) {
					var forward = 0;
					if (player.input.pressingForward) {
						forward = 1;
					} else if (player.input.pressingBack) {
						forward = -1;
					}

					if (mc.options.sprintKey.isPressed() && location == BoatEntity.Location.IN_AIR && !player.input.jumping) {
						y = -speed;
					}

					var forwardDir = new Vec3d(-MathHelper.sin(yaw), 0, MathHelper.cos(yaw));

					Vec3d forwardSpeed = forwardDir.multiply(speed).multiply(forward);

					vehicle.setVelocity(new Vec3d(forwardSpeed.x, y, forwardSpeed.z));
				} else {
					var vel = vehicle.getVelocity();
					vehicle.setVelocity(new Vec3d(vel.x, y, vel.z));
				}
			}
		}
	}

	@Override
	public ImmutableList<IConfigOption> getConfigOptions() {
		return ImmutableList.of(flySpeed, landSpeed);
	}
}
