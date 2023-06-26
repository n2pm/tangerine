package pm.n2.tangerine.gui

import gay.eviee.imguiquilt.ImGuiQuilt
import imgui.ImGui
import imgui.flag.ImGuiKey
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import pm.n2.tangerine.Tangerine

object ImGuiScreen : Screen(Text.of("Tangerine")) {
    var initialized: Boolean = false
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
            for (renderable in ImGuiManager.renderables) ImGuiQuilt.renderstack.add(renderable.renderable)
            initialized = true
        }
    }

    override fun render(graphics: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float) {}

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) shouldClose = true

        val io = ImGui.getIO()

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            io.setKeysDown(io.getKeyMap(ImGuiKey.Backspace), true)
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            io.setKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT,true)
            io.keyShift = true
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            io.setKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL,true)
            io.keyCtrl = true
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            io.setKeysDown(GLFW.GLFW_KEY_LEFT_ALT,true)
            io.keyAlt = true
        }

        return false
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val io = ImGui.getIO()

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            io.setKeysDown(io.getKeyMap(ImGuiKey.Backspace), false)
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            io.setKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT, false)
            io.keyShift = false
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            io.setKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL, false)
            io.keyCtrl = false
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            io.setKeysDown(GLFW.GLFW_KEY_LEFT_ALT, false)
            io.keyAlt = false
        }

        return false
    }


    override fun charTyped(chr: Char, modifiers: Int): Boolean = false
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = false
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean = false

    override fun closeScreen() {
        if (initialized) {
            for (renderable in ImGuiManager.renderables) ImGuiQuilt.renderstack.remove(renderable.renderable)
        }

        Tangerine.config.write()
        super.closeScreen()
        initialized = false
    }
}
