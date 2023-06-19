package pm.n2.tangerine.modules;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigInteger;
import com.adryd.cauldron.api.config.IConfigOption;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.gui.renderables.ConfigWindow;

public class Module {
	public ConfigBoolean enabled;
	public ConfigInteger keybind; // TODO: ConfigKeybind how what?

	public String id;
	public String name;
	public String description;
	public ModuleCategory category;

	protected ConfigWindow configWindow;

	public Module(String id, String name, String description, ModuleCategory category) {
		this.enabled = new ConfigBoolean(id + ".enabled", false);
		this.keybind = new ConfigInteger(id + ".keybind", 0, 0, 255);

		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		this.configWindow = new ConfigWindow(this);

	}

	public void onEnabled() {}
	public void onDisabled() {}
	public void onStartTick(MinecraftClient mc) {}
	public void onEndTick(MinecraftClient mc) {}
	public ImmutableList<IConfigOption> getConfigOptions() {return null;}

	public void showConfigWindow() {
		configWindow.enabled = true;
	}
}
