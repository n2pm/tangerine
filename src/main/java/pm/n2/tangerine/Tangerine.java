package pm.n2.tangerine;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pm.n2.tangerine.gui.ImGuiManager;
import pm.n2.tangerine.gui.ImGuiScreen;
import pm.n2.tangerine.gui.renderables.AboutWindow;
import pm.n2.tangerine.gui.renderables.DemoWindow;
import pm.n2.tangerine.gui.renderables.MenuBar;

public class Tangerine implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Tangerine");
	public static final ImGuiManager IMGUI_MANAGER = new ImGuiManager();
	public static final ImGuiScreen IMGUI_SCREEN = new ImGuiScreen(IMGUI_MANAGER);
	public static final ModuleState MODULE_STATE = new ModuleState();
	public static String MOD_VERSION = "";

	@Override
	public void onInitializeClient(ModContainer mod) {
		MOD_VERSION = mod.metadata().version().raw();

		IMGUI_MANAGER.addRenderable(new MenuBar());
		IMGUI_MANAGER.addRenderable(new DemoWindow());
		IMGUI_MANAGER.addRenderable(new AboutWindow());

		ClientTickEvents.END.register(mc -> {
			if (mc.player != null) {
				if (MODULE_STATE.flight && !mc.player.isSpectator() && !mc.player.isCreative()) {
					mc.player.getAbilities().allowFlying = true;
				}
			}
		});
	}
}
