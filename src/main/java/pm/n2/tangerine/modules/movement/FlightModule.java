package pm.n2.tangerine.modules.movement;

import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class FlightModule extends Module {
	private int flightTicks = 0;
	private boolean flightBypass = false;

	public FlightModule() {
		super("Flight", "Allows you to fly", ModuleCategory.MOVEMENT);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null && this.enabled) {
			if (flightBypass && flightTicks >= 80) {
				//Tangerine.LOGGER.info("Flight: Disabling flight for 1 tick");
				mc.player.getAbilities().flying = false;
				flightTicks = -3; // this is a hack
			}

			if (flightBypass && flightTicks == -1 && !mc.player.isSpectator() && !mc.player.isCreative()) {
				//Tangerine.LOGGER.info("Flight: Enabling flight after 1 tick");
				mc.player.getAbilities().flying = true;
			}

			if (!mc.player.isSpectator() && !mc.player.isCreative()) {
				mc.player.getAbilities().allowFlying = true;
			}

			//Tangerine.LOGGER.info("Flying: " + mc.player.getAbilities().flying);
			if (flightBypass && (mc.player.getAbilities().flying || flightTicks < 0)) {
				flightTicks++;
				//Tangerine.LOGGER.info("Flight ticks: " + flightTicks);
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

		flightTicks = 0;
	}
}
