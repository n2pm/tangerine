package pm.n2.tangerine.modules.misc;

import gay.eviee.imguiquilt.ImGuiQuilt;
import gay.eviee.imguiquilt.interfaces.Renderable;
import gay.eviee.imguiquilt.interfaces.Theme;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class ModuleListModule extends Module {
	private boolean shouldDraw = false;

	private Renderable renderable = new Renderable() {
		@Override
		public String getName() {
			return "Tangerine Module List";
		}

		@Override
		public Theme getTheme() {
			return null;
		}

		@Override
		public void render() {
			if (!shouldDraw) return;

			var windowFlags = ImGuiWindowFlags.NoDecoration |
					ImGuiWindowFlags.NoInputs |
					ImGuiWindowFlags.NoBackground |
					ImGuiWindowFlags.NoBringToFrontOnFocus |
					ImGuiWindowFlags.NoFocusOnAppearing;

			StringBuilder moduleListString = new StringBuilder();

			for (var module : Tangerine.MODULE_MANAGER.getModules()) {
				if (module.enabled) moduleListString.append(module.name).append("\n");
			}

			var screenSize = ImGui.getMainViewport().getSize();
			var screenPos = ImGui.getMainViewport().getPos();
			var size = new ImVec2();
			ImGui.calcTextSize(size, moduleListString.toString());
			size = new ImVec2(Math.min(screenSize.x, size.x + 25), Math.min(screenSize.y, size.y + 25));

			ImGui.setNextWindowSize(size.x, size.y);
			ImGui.setNextWindowPos(screenPos.x + (screenSize.x - size.x), screenPos.y);
			if (ImGui.begin("##Tangerine Module List", windowFlags)) {
				ImGui.textUnformatted(moduleListString.toString());
			}

			ImGui.end();
		}
	};

	public ModuleListModule() {
		super("Module list", "Shows a list of all enabled modules", ModuleCategory.MISC);
	}

	@Override
	public void onEndTick(MinecraftClient mc) {
		var screen = mc.currentScreen;
		var lastShouldDraw = this.shouldDraw;

		if (screen == null) {
			this.shouldDraw = true;
		} else {
			this.shouldDraw = false;
		}

		if (this.shouldDraw != lastShouldDraw) {
			if (this.shouldDraw) {
				ImGuiQuilt.pushRenderable(this.renderable);
			} else {
				ImGuiQuilt.pullRenderable(this.renderable);
			}
		}
	}
}
