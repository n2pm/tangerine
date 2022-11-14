package pm.n2.tangerine.modules.movement;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigDouble;
import com.adryd.cauldron.api.config.IConfigOption;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import pm.n2.tangerine.mixin.ClientConnectionInvoker;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class FlightModule extends Module {
	public ConfigBoolean flyKickBypass = new ConfigBoolean("flight_fly_kick", true);
	public ConfigBoolean flyScrollSpeed = new ConfigBoolean("flight_scroll_speed", false);
	public ConfigBoolean flyFriction = new ConfigBoolean("flight_friction", false);

	public ConfigDouble flySpeed = new ConfigDouble("flight_speed", 1.0, 0.1, 20.0);

	private int fallingTicks = 0;
	private Vec3d lastPos;

	public FlightModule() {
		super("flight", "Flight", "Allows you to fly", ModuleCategory.MOVEMENT);
	}

	@Override
	public ImmutableList<IConfigOption> getConfigOptions() {
		return ImmutableList.of(flyKickBypass, flyScrollSpeed, flyFriction, flySpeed);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null && this.enabled.getBooleanValue()) {
			var abilities = mc.player.getAbilities();
			abilities.setFlySpeed(((float) flySpeed.getDoubleValue()) / 20);

			var canNormallyFly = mc.player.isSpectator() || mc.player.isCreative();
			if (!canNormallyFly) {
				abilities.allowFlying = true;
			}

			if (abilities.flying && !canNormallyFly) {
				fallingTicks++;

				if (fallingTicks >= 20) {
					var shouldAntiKick = flyKickBypass.getBooleanValue();

					// are we descending?
					if (mc.player.isSneaking()) {
						if (lastPos != null && mc.player.getY() < lastPos.getY())
							shouldAntiKick = false;
					}

					if (shouldAntiKick) {
						var packet = new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.03126, mc.player.getZ(), true);
						((ClientConnectionInvoker) mc.player.networkHandler.getConnection()).invokeSendImmediately(packet, null);
					}

					fallingTicks = 0;
				}
			}
		}
	}

	@Override
	public void onStartTick(MinecraftClient mc) {
		if (mc.player != null && this.enabled.getBooleanValue()) {
			var abilities = mc.player.getAbilities();
			var canNormallyFly = mc.player.isSpectator() || mc.player.isCreative();

			if (abilities.flying && !canNormallyFly) {
				lastPos = mc.player.getPos();
			}
		}
	}

	@Override
	public void onDisabled() {
		var mc = MinecraftClient.getInstance();
		if (mc.player != null) {
			if (!mc.player.isSpectator() && !mc.player.isCreative()) {
				mc.player.getAbilities().allowFlying = false;
				mc.player.getAbilities().flying = false;
			}
		}
	}
}
