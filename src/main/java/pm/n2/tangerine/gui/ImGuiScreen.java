package pm.n2.tangerine.gui;

import gay.eviee.imguiquilt.ImGuiQuilt;
import gay.eviee.imguiquilt.imgui.ImguiLoader;
import imgui.ImGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pm.n2.tangerine.Tangerine;

public class ImGuiScreen extends Screen {
	private final ImGuiManager manager;
	public boolean shouldClose;
	private static boolean initialized;

	public ImGuiScreen(ImGuiManager manager) {
		super(Text.of("Tangerine ImGui Screen"));
		this.manager = manager;
	}

	@Override
	public void tick() {
		if (shouldClose) {
			shouldClose = false;
			closeScreen();
		}

		super.tick();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void init() {
		if (!initialized) {
			for (TangerineRenderable renderable : manager.renderables) {
				ImGuiQuilt.renderstack.add(renderable.renderable);
			}

			ImGui.getIO().setWantCaptureKeyboard(true);

			initialized = true;
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
		//ImGuiLoader.keyPressed(keyCode, scanCode, modifiers);

		ImGui.getIO().setKeysDown(keyCode, true);

		if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			shouldClose = true;
		}

		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		//return (ImguiLoader.keyReleased(keyCode, scanCode, modifiers)) && super.keyReleased(keyCode, scanCode, modifiers);
		//ImguiLoader.keyReleased(keyCode, scanCode, modifiers);

		ImGui.getIO().setKeysDown(keyCode, false);

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
		if (initialized) {
			for (TangerineRenderable tangerineRenderable : manager.renderables) {
				ImGuiQuilt.renderstack.remove(tangerineRenderable.renderable);
			}

			ImGui.getIO().setWantCaptureKeyboard(false);
		}

		Tangerine.CONFIG.write();

		super.closeScreen();
		initialized = false;
	}
}
