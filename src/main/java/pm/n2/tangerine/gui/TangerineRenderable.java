package pm.n2.tangerine.gui;

import gay.eviee.imguiquilt.interfaces.Renderable;
import gay.eviee.imguiquilt.interfaces.Theme;

public class TangerineRenderable {
	public final String name;
	public boolean enabled;

	public ImGuiManager manager;

	public Renderable renderable = new Renderable() {
		@Override
		public String getName() {
			return name;
		}

		@Override
		public Theme getTheme() {
			return ImGuiManager.theme;
		}

		@Override
		public void render() {
			if (enabled) draw();
		}
	};

	public TangerineRenderable(String name) {
		this.name = name;
		this.enabled = true;
	}

	public TangerineRenderable(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	public void draw() {

	}
}
