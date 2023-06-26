package pm.n2.tangerine.gui

import gay.eviee.imguiquilt.ImGuiQuilt
import gay.eviee.imguiquilt.imgui.ImguiLoader
import imgui.ImGui
import imgui.flag.ImGuiKey
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import pm.n2.tangerine.Tangerine

class ImGuiScreen : Screen(Text.of("Tangerine")) {
    companion object {
        var initialized: Boolean = false
    }

    var shouldClose: Boolean = false

    override fun tick() {
        if (shouldClose) {
            shouldClose = false
            closeScreen()
        }

        super.tick()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun init() {
        if (!initialized) {
            for (renderable in Tangerine.imguiManager.renderables) ImGuiQuilt.renderstack.add(renderable.renderable)
            initialized = true
        }
    }

    override fun render(graphics: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float) {}

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) shouldClose = true
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean = false
    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = false
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean = false

    override fun closeScreen() {
        if (initialized) {
            for (renderable in Tangerine.imguiManager.renderables) ImGuiQuilt.renderstack.remove(renderable.renderable)
        }

        Tangerine.config.write()
        super.closeScreen()
        initialized = false
    }
}
