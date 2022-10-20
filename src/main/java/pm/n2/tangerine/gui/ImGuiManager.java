package pm.n2.tangerine.gui;

import java.util.ArrayList;
import java.util.List;

public class ImGuiManager {
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
