package pm.n2.tangerine.gui;

import gay.eviee.imguiquilt.interfaces.Theme;
import imgui.ImFont;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class ImGuiManager {
	private static ImFont font;

	public static Theme theme = new Theme() {
		@Override
		public void preRender() {
			if (font != null) {
				ImGui.pushFont(font);
			}
			//ImGui.pushStyleColor(ImGuiCol.Tab, ImGui.getColorU32(203f / 255f, 32f / 255f, 39f / 255f, 1));
		}

		@Override
		public void postRender() {
			if (font != null) {
				ImGui.popFont();
			}
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

	public void setFont(ImFont font) {
		ImGuiManager.font = font;
	}
}
