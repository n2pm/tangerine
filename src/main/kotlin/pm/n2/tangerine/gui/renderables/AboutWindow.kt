package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.type.ImBoolean
import net.minecraft.client.sound.EntityTrackingSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Util
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.gui.TangerineRenderable

object AboutWindow : TangerineRenderable("AboutWindow", false) {
    private var stal: EntityTrackingSoundInstance? = null
    private var lastEnabled = false

    private fun onOpen() {
        stal = EntityTrackingSoundInstance(
            SoundEvents.MUSIC_DISC_STAL,
            SoundCategory.RECORDS,
            1f,
            1f,
            Tangerine.mc.player,
            0
        )

        Tangerine.mc.soundManager.play(stal)
    }

    private fun onClose() {
        Tangerine.mc.soundManager.stop(stal)
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
