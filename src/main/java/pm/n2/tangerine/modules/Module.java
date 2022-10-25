package pm.n2.tangerine.modules;

import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.gui.renderables.ConfigWindow;

import javax.annotation.Nullable;
import java.util.Optional;

public class Module {
	public boolean enabled = false;

	public String name;
	public String description;
	public ModuleCategory category;

	private ConfigWindow configWindow;
	public int keybind;

	public Module(String name, String description, ModuleCategory category) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.configWindow = new ConfigWindow(this);
	}

	public void onEnabled() { }
	public void onDisabled() { }
	public void onStartTick(MinecraftClient mc) { }
	public void onEndTick(MinecraftClient mc) { }

	public void showConfigWindow() {
		configWindow.enabled = true;
	}
}
