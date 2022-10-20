package pm.n2.tangerine.gui;

import gay.eviee.imguiquilt.ImGuiQuilt;
import gay.eviee.imguiquilt.imgui.ImguiLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pm.n2.tangerine.Tangerine;

public class ImGuiScreen extends Screen {
	private final ImGuiManager manager;
	public boolean shouldClose;

	public ImGuiScreen(ImGuiManager manager) {
		super(Text.of("Tangerine ImGui Screen"));
		this.manager = manager;
	}

	@Override
	public void tick() {
		if (shouldClose) {
			shouldClose = false;
			Tangerine.imguiScreenOpen = false;
			closeScreen();
		}
	}

	@Override
	public void init() {
		for (TangerineRenderable renderable : manager.renderables) {
			ImGuiQuilt.renderstack.add(renderable.renderable);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		// noop
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		//return (ImguiLoader.charTyped(chr, keyCode) && super.charTyped(chr, keyCode));
		ImguiLoader.charTyped(chr, keyCode);
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		//return (ImguiLoader.keyPressed(keyCode, scanCode, modifiers) && super.keyPressed(keyCode, scanCode, modifiers));
		ImguiLoader.keyPressed(keyCode, scanCode, modifiers);
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			shouldClose = true;
		}

		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		//return (ImguiLoader.keyReleased(keyCode, scanCode, modifiers)) && super.keyReleased(keyCode, scanCode, modifiers);
		ImguiLoader.keyReleased(keyCode, scanCode, modifiers);
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	public void closeScreen() {
		for (TangerineRenderable tangerineRenderable : manager.renderables) {
			ImGuiQuilt.renderstack.remove(tangerineRenderable.renderable);
		}

		super.closeScreen();
	}
}
