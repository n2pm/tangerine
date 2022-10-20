package pm.n2.tangerine.mixin;

import gay.eviee.imguiquilt.ImGuiQuilt;
import gay.eviee.imguiquilt.interfaces.Renderable;
import gay.eviee.imguiquilt.interfaces.Theme;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import pm.n2.tangerine.Tangerine;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	private Renderable renderable = new Renderable() {
		@Override
		public String getName() {
			return "Tangerine";
		}

		@Override
		public Theme getTheme() {
			return null;
		}

		@Override
		public void render() {
			var windowFlags = ImGuiWindowFlags.NoDecoration |
					ImGuiWindowFlags.NoInputs |
					ImGuiWindowFlags.NoBackground |
					ImGuiWindowFlags.NoBringToFrontOnFocus |
					ImGuiWindowFlags.NoFocusOnAppearing;
			var text = String.format("tangerine %s\nmade with love by notnet", Tangerine.MOD_VERSION);

			// ???
			var size = new ImVec2();
			ImGui.calcTextSize(size, text);
			// not sure why i have to do this but i did it in cl_showpos so ???????
			size = new ImVec2(size.x + 25, size.y + 25);

			ImGui.setNextWindowSize(size.x, size.y);

			var windowCorner = ImGui.getMainViewport().getPos();
			ImGui.setNextWindowPos(windowCorner.x, windowCorner.y);

			if (ImGui.begin("##Tangerine Splash Text", windowFlags)) {
				ImGui.textUnformatted(text);
			}

			ImGui.end();
		}
	};

	@Inject(method = "init", at = @At("TAIL"))
	public void tangerine$onInit(CallbackInfo ci) {
		if (!ImGuiQuilt.renderstack.contains(renderable)) {
			ImGuiQuilt.renderstack.add(renderable);
		}
	}

	@Inject(method = "removed", at = @At("HEAD"))
	public void tangerine$onRemoved(CallbackInfo ci) {
		ImGuiQuilt.renderstack.remove(renderable);
	}
}
