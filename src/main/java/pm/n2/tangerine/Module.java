package pm.n2.tangerine;

import net.minecraft.client.MinecraftClient;

public class Module {
	public boolean enabled = false;

	public String name;
	public String description;
	public ModuleCategory category;

	public Module(String name, String description, ModuleCategory category) {
		this.name = name;
		this.description = description;
		this.category = category;
	}

	public void onEnabled() { }
	public void onDisabled() { }
	public void onStartTick(MinecraftClient mc) { }
	public void onEndTick(MinecraftClient mc) { }
}
