package pm.n2.tangerine.mixin;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.Tangerine;
import pm.n2.tangerine.gui.TangerineRenderable;
import pm.n2.tangerine.managers.ImGuiManager;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Unique
    private static TangerineRenderable renderable = new TangerineRenderable("TangerineTitleScreen") {
        @Override
        public void draw() {
            var windowFlags = ImGuiWindowFlags.NoDecoration |
                ImGuiWindowFlags.NoInputs |
                ImGuiWindowFlags.NoBackground |
                ImGuiWindowFlags.NoBringToFrontOnFocus |
                ImGuiWindowFlags.NoFocusOnAppearing;
            var text = String.format("tangerine %s", Tangerine.INSTANCE.getVersion());

            // ???
            var size = new ImVec2();
            ImGui.calcTextSize(size, text);

            // not sure why i have to do this but i did it in cl_showpos so ???????
            size = new ImVec2(size.x + 25, size.y + 25);

            ImGui.setNextWindowSize(size.x, size.y);

            var windowCorner = ImGui.getMainViewport().getPos();
            ImGui.setNextWindowPos(windowCorner.x, windowCorner.y);

            if (ImGui.begin("##Tangerine Splash Text", windowFlags)) {
                ImGui.textUnformatted(text);
            }

            ImGui.end();
        }
    };

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void tangerine$onDisplayed(CallbackInfo ci) {
        ImGuiManager.INSTANCE.addInstantRenderable(renderable);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    public void tangerine$onRemoved(CallbackInfo ci) {
        ImGuiManager.INSTANCE.removeInstantRenderable(renderable);
    }
}
