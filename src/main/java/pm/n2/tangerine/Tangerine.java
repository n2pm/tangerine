package pm.n2.tangerine;

import com.adryd.cauldron.api.config.ConfigFile;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pm.n2.tangerine.gui.ImGuiManager;
import pm.n2.tangerine.gui.ImGuiScreen;
import pm.n2.tangerine.gui.renderables.AboutWindow;
import pm.n2.tangerine.gui.renderables.DemoWindow;
import pm.n2.tangerine.gui.renderables.MenuBar;
import pm.n2.tangerine.modules.ModuleManager;

public class Tangerine implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Tangerine");
	public static String MOD_VERSION;
	public static final String MOD_ID = "tangerine";

	public static final ImGuiManager IMGUI_MANAGER = new ImGuiManager();
	public static final ImGuiScreen IMGUI_SCREEN = new ImGuiScreen(IMGUI_MANAGER);
	public static final ModuleManager MODULE_MANAGER = new ModuleManager();

	public static final ConfigFile CONFIG = new ConfigFile(MOD_ID);

	@Override
	public void onInitializeClient(ModContainer mod) {
		MOD_VERSION = mod.metadata().version().raw();

		IMGUI_MANAGER.addRenderable(new MenuBar());
		IMGUI_MANAGER.addRenderable(new DemoWindow());
		IMGUI_MANAGER.addRenderable(new AboutWindow());

		MODULE_MANAGER.registerModules();

		ClientTickEvents.START.register(mc -> {
			for (var module : MODULE_MANAGER.getModules()) {
				module.onStartTick(mc);
			}
		});

		ClientTickEvents.END.register(mc -> {
			for (var module : MODULE_MANAGER.getModules()) {
				module.onEndTick(mc);
			}
		});

		for (var module : MODULE_MANAGER.getModules()) {
			CONFIG.addConfig(module.enabled);
			var opts = module.getConfigOptions();
			if (opts != null)
				CONFIG.addConfigs(opts);
		}
		CONFIG.read();

		ClientLifecycleEvents.STOPPING.register(mc -> CONFIG.write());
	}
}
