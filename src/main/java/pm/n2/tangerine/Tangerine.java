package pm.n2.tangerine;

import com.adryd.cauldron.api.config.ConfigFile;
import com.mojang.blaze3d.platform.TextureUtil;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGui;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pm.n2.tangerine.commands.CommandManager;
import pm.n2.tangerine.gui.ImGuiManager;
import pm.n2.tangerine.gui.ImGuiScreen;
import pm.n2.tangerine.gui.renderables.AboutWindow;
import pm.n2.tangerine.gui.renderables.DemoWindow;
import pm.n2.tangerine.gui.renderables.MenuBar;
import pm.n2.tangerine.modules.ModuleManager;
import pm.n2.tangerine.modules.misc.UnifontModule;

import java.nio.ByteBuffer;

public class Tangerine implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Tangerine");
	public static String MOD_VERSION;
	public static final String MOD_ID = "tangerine";

	public static final ImGuiManager IMGUI_MANAGER = new ImGuiManager();
	public static final ImGuiScreen IMGUI_SCREEN = new ImGuiScreen(IMGUI_MANAGER);
	public static ImFont IMGUI_FONT_DEFAULT;
	public static ImFont IMGUI_FONT_UNIFONT;

	public static final ModuleManager MODULE_MANAGER = new ModuleManager();
	public static final CommandManager COMMAND_MANAGER = new CommandManager();

	public static final ConfigFile CONFIG = new ConfigFile(MOD_ID);

	@Override
	public void onInitializeClient(ModContainer mod) {
		MOD_VERSION = mod.metadata().version().raw();

		IMGUI_MANAGER.addRenderable(new MenuBar());
		IMGUI_MANAGER.addRenderable(new DemoWindow());
		IMGUI_MANAGER.addRenderable(new AboutWindow());

		MODULE_MANAGER.registerModules();
		COMMAND_MANAGER.registerCommands();

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

		ClientLifecycleEvents.STOPPING.register(mc -> CONFIG.write());

		var useUnifont = MODULE_MANAGER.get(UnifontModule.class).enabled.getBooleanValue();
		//if (useUnifont) {
			try {
				var ctx = ImGui.createContext();
				ImGui.setCurrentContext(ctx);

				var io = ImGui.getIO();
				var fonts = io.getFonts();

				var fontStream = Tangerine.class.getResourceAsStream("/assets/tangerine/unifont.otf");
				if (fontStream != null) {
					ByteBuffer buffer = TextureUtil.readResource(fontStream);
					buffer.flip();
					byte[] arr = new byte[buffer.remaining()];
					buffer.get(arr);

					ImFontConfig fontConfig = new ImFontConfig();

					IMGUI_FONT_DEFAULT = fonts.addFontDefault(fontConfig);
					IMGUI_FONT_UNIFONT = fonts.addFontFromMemoryTTF(arr, 16, fontConfig);
					fonts.build();

					fontConfig.destroy();

					if (useUnifont) {
						IMGUI_MANAGER.setFont(IMGUI_FONT_UNIFONT);
					} else {
						IMGUI_MANAGER.setFont(IMGUI_FONT_DEFAULT);
					}
				} else {
					LOGGER.info("font stream null");
				}

				// causes "free(): invalid size" crash :(
				// jni can have a bit of used once memory as a treat
				//ImGui.destroyContext(ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
	}
}
