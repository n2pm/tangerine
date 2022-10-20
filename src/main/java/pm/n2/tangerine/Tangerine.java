package pm.n2.tangerine;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pm.n2.tangerine.gui.ImGuiManager;
import pm.n2.tangerine.gui.ImGuiScreen;
import pm.n2.tangerine.gui.renderables.AboutWindow;
import pm.n2.tangerine.gui.renderables.DemoWindow;
import pm.n2.tangerine.gui.renderables.MenuBar;

public class Tangerine implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Tangerine");
	public static String MOD_VERSION = "";

	public static final ImGuiManager IMGUI_MANAGER = new ImGuiManager();
	public static final ImGuiScreen IMGUI_SCREEN = new ImGuiScreen(IMGUI_MANAGER);

	@Override
	public void onInitializeClient(ModContainer mod) {
		MOD_VERSION = mod.metadata().version().raw();

		IMGUI_MANAGER.addRenderable(new MenuBar());
		IMGUI_MANAGER.addRenderable(new DemoWindow());
		IMGUI_MANAGER.addRenderable(new AboutWindow());
	}
}
