package pm.n2.tangerine.modules.movement;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigOptionBase;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.renderables.ConfigWindow;
import pm.n2.tangerine.mixin.ClientConnectionInvoker;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class FlightModule extends Module {
	public ConfigBoolean flyKickBypass = new ConfigBoolean("Fly kick bypass", true);
	public ConfigBoolean flyScrollSpeed = new ConfigBoolean("Fly scroll speed", false);
	public ConfigBoolean flyFriction = new ConfigBoolean("Fly friction", false);

	private int fallingTicks = 0;
	private Vec3d lastPos;

	public FlightModule() {
		super("Flight", "Allows you to fly", ModuleCategory.MOVEMENT);
	}

	@Override
	public ImmutableList<ConfigOptionBase> getConfigOptions() {
		return ImmutableList.of(flyKickBypass, flyScrollSpeed, flyFriction);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null && this.enabled) {
			var abilities = mc.player.getAbilities();
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
		if (mc.player != null && this.enabled) {
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
