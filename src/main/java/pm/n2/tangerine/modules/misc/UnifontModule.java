package pm.n2.tangerine.modules.misc;

import com.mojang.blaze3d.platform.TextureUtil;
import imgui.ImFontConfig;
import imgui.ImGui;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;
import static pm.n2.tangerine.Tangerine.IMGUI_MANAGER;

public class UnifontModule extends Module {
	public UnifontModule() {
		super("unifont", "Unifont", "Makes all text use the Unifont font", ModuleCategory.MISC);
	}

	@Override
	public void onEnabled() {
		if (Tangerine.IMGUI_FONT_UNIFONT != null) {
			IMGUI_MANAGER.setFont(Tangerine.IMGUI_FONT_UNIFONT);
		}
	}

	@Override
	public void onDisabled() {
		if (Tangerine.IMGUI_FONT_DEFAULT != null) {
			IMGUI_MANAGER.setFont(Tangerine.IMGUI_FONT_DEFAULT);
		}
	}
}
