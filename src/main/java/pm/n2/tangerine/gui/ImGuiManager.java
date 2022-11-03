package pm.n2.tangerine.gui;

import gay.eviee.imguiquilt.interfaces.Theme;
import imgui.ImFont;
import imgui.ImGui;
import pm.n2.tangerine.Tangerine;

import java.util.ArrayList;
import java.util.List;

public class ImGuiManager {
	public static Theme theme = new Theme() {
		@Override
		public void preRender() {
			//ImGui.pushStyleColor(ImGuiCol.Tab, ImGui.getColorU32(203f / 255f, 32f / 255f, 39f / 255f, 1));
		}

		@Override
		public void postRender() {
			//ImGui.popStyleColor();
		}
	};

	public List<TangerineRenderable> renderables = new ArrayList<>();

	public void addRenderable(TangerineRenderable tangerineRenderable) {
		tangerineRenderable.manager = this;
		renderables.add(tangerineRenderable);
	}

	public TangerineRenderable get(String name) {
		return renderables.stream()
				.filter(tangerineRenderable -> tangerineRenderable.name == name)
				.findFirst().orElse(null);
	}
}
