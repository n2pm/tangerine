package pm.n2.tangerine.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TestScreen extends Screen {
	public TestScreen() {
		super(Text.empty());
	}

	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
	}

	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
