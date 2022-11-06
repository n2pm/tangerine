package pm.n2.tangerine.modules;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigOptionBase;
import com.adryd.cauldron.api.config.IConfigOption;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.gui.renderables.ConfigWindow;

import javax.annotation.Nullable;
import java.util.Optional;

public class Module {
	public ConfigBoolean enabled;

	public String id;
	public String name;
	public String description;
	public ModuleCategory category;

	protected ConfigWindow configWindow;
	public int keybind;

	public Module(String id, String name, String description, ModuleCategory category) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		this.configWindow = new ConfigWindow(this);
		this.enabled = new ConfigBoolean(id + "_enabled", false);
	}

	public void onEnabled() { }
	public void onDisabled() { }
	public void onStartTick(MinecraftClient mc) { }
	public void onEndTick(MinecraftClient mc) { }
	public ImmutableList<IConfigOption> getConfigOptions() { return null; }

	public void showConfigWindow() {
		configWindow.enabled = true;
	}
}
