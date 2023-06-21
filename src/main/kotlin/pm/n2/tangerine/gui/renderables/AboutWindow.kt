package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.type.ImBoolean
import net.minecraft.client.MinecraftClient
import net.minecraft.registry.Holder
import net.minecraft.sound.MusicSound
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Util
import pm.n2.tangerine.gui.TangerineRenderable

class AboutWindow : TangerineRenderable("AboutWindow", false) {
    private val stal = MusicSound(
            Holder.createDirect(SoundEvents.MUSIC_DISC_STAL),
            0,
            0,
            true
    )
    private var lastEnabled = false

    private fun onOpen() {
        MinecraftClient.getInstance().musicTracker.play(stal)
    }

    private fun onClose() {
        val musicTracker = MinecraftClient.getInstance().musicTracker
        if (musicTracker.isPlayingMusic(stal)) musicTracker.stopPlaying()
    }

    override fun draw() {
        val enabled = ImBoolean(this.enabled)

        if (ImGui.begin("About Tangerine", enabled)) {
            ImGui.textUnformatted("Tangerine, a NotNet project")
            ImGui.textUnformatted("Powered by Cauldron and imgui-quilt")
            ImGui.textUnformatted("(c) NotNite, adryd, Cynosphere, and Murmiration, 2023")
            if (ImGui.button("Open source code")) {
                Util.getOperatingSystem().open("https://github.com/n2pm/tangerine")
            }
            if (ImGui.button("Crash game")) {
                throw RuntimeException("She strogan my beef til im off")
            }
        }

        ImGui.end()

        this.enabled = enabled.get()
        if (this.enabled != lastEnabled) {
            if (this.enabled) onOpen() else onClose()
            lastEnabled = this.enabled
        }
        lastEnabled = this.enabled
    }
}
