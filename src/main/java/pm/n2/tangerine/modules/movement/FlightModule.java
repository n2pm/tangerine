package pm.n2.tangerine.modules.movement;

import com.adryd.cauldron.api.config.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import pm.n2.tangerine.mixin.ClientConnectionInvoker;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class FlightModule extends Module {
	public ConfigBoolean flyKickBypass = new ConfigBoolean("Fly kick bypass", false);
	public ConfigBoolean flyScrollSpeed = new ConfigBoolean("Fly scroll speed", false);
	public ConfigBoolean flyFriction = new ConfigBoolean("Fly friction", false);

	private int fallingTicks = 0;

	public FlightModule() {
		super("Flight", "Allows you to fly", ModuleCategory.MOVEMENT);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null && this.enabled) {
			if (!mc.player.isSpectator() && !mc.player.isCreative()) {
				mc.player.getAbilities().allowFlying = true;
			}

			fallingTicks++;

			if (fallingTicks >= 20) {
				var packet = new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.05, mc.player.getZ(), true);
				((ClientConnectionInvoker) mc.player.networkHandler.getConnection()).invokeSendImmediately(packet, null);

				fallingTicks = 0;
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
