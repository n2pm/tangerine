package pm.n2.tangerine.modules.misc

import com.mojang.blaze3d.platform.TextureUtil
import imgui.ImFontConfig
import imgui.ImGui
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.config.DoubleConfigOption
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.managers.ImGuiManager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object GUIModule : Module("gui", ModuleCategory.MISC) {
    val unifont = BooleanConfigOption(id, "unifont", false)
    val fontScale = DoubleConfigOption(id, "font_scale", 1.0, 0.1, 5.0)

    override val configOptions = listOf(unifont, fontScale)
    override val shouldHideEnabled = true

    override fun init() {
        doFonts(unifont.value)
        Tangerine.eventManager.registerFunc(TangerineEvent.ImGuiDraw::class) { _, unregister ->
            ImGui.getIO().fontGlobalScale = fontScale.value.toFloat()
            unregister()
        }

        unifont.valueCallback = {
            ImGuiManager.font = if (it) ImGuiManager.fontUnifont else ImGuiManager.fontDefault
        }

        fontScale.valueCallback = {
            ImGui.getIO().fontGlobalScale = it.toFloat()
        }
    }

    private fun doFonts(useUnifont: Boolean) {
        try {
            val ctx = ImGui.createContext()
            ImGui.setCurrentContext(ctx)

            val io = ImGui.getIO()
            val fonts = io.fonts

            val fontStream = Tangerine::class.java.getResourceAsStream("/assets/tangerine/unifont.otf")
            if (fontStream != null) {
                val buffer = TextureUtil.readResource(fontStream)
                buffer.flip()
                val arr = ByteArray(buffer.remaining())
                buffer.get(arr)

                val fontConfig = ImFontConfig()
                ImGuiManager.fontDefault = fonts.addFontDefault(fontConfig)
                ImGuiManager.fontUnifont = fonts.addFontFromMemoryTTF(arr, 16f, fontConfig)

                fonts.build()
                fontConfig.destroy()

                ImGuiManager.font = if (useUnifont) ImGuiManager.fontUnifont else ImGuiManager.fontDefault
            } else {
                Tangerine.logger.warn("Unifont font stream is null?")
            }
        } catch (e: Exception) {
            Tangerine.logger.error("Failed to load font", e)
        }
    }
}
