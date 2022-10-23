package pm.n2.tangerine.modules;

import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.Module;
import pm.n2.tangerine.ModuleCategory;

public class FlightModule extends Module {
	public FlightModule() {
		super("Flight", "Allows you to fly", ModuleCategory.MOVEMENT);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		if (mc.player != null) {
			if (this.enabled && !mc.player.isSpectator() && !mc.player.isCreative()) {
				mc.player.getAbilities().allowFlying = true;
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
