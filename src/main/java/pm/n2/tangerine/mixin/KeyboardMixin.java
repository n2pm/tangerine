package pm.n2.tangerine.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.KeyboardManager;
import pm.n2.tangerine.gui.ImGuiManager;
import pm.n2.tangerine.gui.ImGuiScreen;

@ClientOnly
@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey(JIIII)V", at = @At("HEAD"), cancellable = true)
    public void tangerine$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS) {
            if (KeyboardManager.INSTANCE.registerKeyPress(key)) {
                ci.cancel();
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            KeyboardManager.INSTANCE.registerKeyRelease(key);
        }

        var menuKey = GLFW.GLFW_KEY_RIGHT_SHIFT;
        var keybind = ImGuiManager.INSTANCE.getOpened().getKeybind();
        if (keybind != null) menuKey = keybind.getKey();

        if (key == menuKey) {
            var mc = MinecraftClient.getInstance();
            var screen = ImGuiScreen.INSTANCE;
            if (mc.currentScreen == null) mc.setScreen(screen);
        }
    }
}
