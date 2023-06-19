package pm.n2.tangerine.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;

@ClientOnly
@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Inject(method = "onKey(JIIII)V", at = @At("HEAD"))
	public void tangerine$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (action == GLFW.GLFW_PRESS) {
			Tangerine.KEYBOARD_MANAGER.keyPress(key);
		} else if (action == GLFW.GLFW_RELEASE) {
			Tangerine.KEYBOARD_MANAGER.keyRelease(key);
		}

		if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			var mc = MinecraftClient.getInstance();
			var screen = Tangerine.IMGUI_SCREEN;

			if (mc.currentScreen == null) {
				mc.setScreen(screen);
			}
		}
	}
}
