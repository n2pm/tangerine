package pm.n2.tangerine.modules.automation;

import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class AutowalkModule extends Module {
	public AutowalkModule() {
		super("autowalk", "Autowalk", "w", ModuleCategory.AUTOMATION);
	}

	@Override
	public void onStartTick(MinecraftClient mc) {
		if (this.enabled.getBooleanValue()) {
			mc.options.forwardKey.setPressed(true);
		}
	}

	@Override
	public void onDisabled() {
		var mc = MinecraftClient.getInstance();
		mc.options.forwardKey.setPressed(false);
	}
}
