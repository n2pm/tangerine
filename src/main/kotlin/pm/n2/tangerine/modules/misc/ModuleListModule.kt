package pm.n2.tangerine.modules.misc

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiWindowFlags
import net.minecraft.client.resource.language.I18n
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.EnumConfigOption
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.managers.ModuleManager
import pm.n2.tangerine.gui.GuiUtils
import pm.n2.tangerine.gui.renderables.ConfigWindow
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object ModuleListModule : Module("module_list", ModuleCategory.MISC) {
    val position = EnumConfigOption(id, "position", ModuleListPosition.BOTTOM_RIGHT)
    override val configOptions = listOf(position)
    override val configWindow = ModuleListConfigWindow()

    private var shouldDraw = false

    @EventHandler
    fun drawImGui(event: TangerineEvent.ImGuiDraw) {
        if (!shouldDraw) return
        if (!ModuleListModule.enabled.value) return

        val windowFlags = ImGuiWindowFlags.NoDecoration or
            ImGuiWindowFlags.NoInputs or
            ImGuiWindowFlags.NoBringToFrontOnFocus or
            ImGuiWindowFlags.NoFocusOnAppearing

        val moduleListString = StringBuilder()
        var anyModulesEnabled = false

        for (module in ModuleManager.modules) {
            if (module.enabled.value) {
                anyModulesEnabled = true
                moduleListString.append(I18n.translate("tangerine.module.${module.id}.name")).append("\n")
            }
        }

        if (!anyModulesEnabled) return

        val screenSize = ImGui.getMainViewport().size
        val screenPos = ImGui.getMainViewport().pos
        var size = ImVec2()

        ImGui.calcTextSize(size, moduleListString.toString())
        size = ImVec2(screenSize.x.coerceAtMost(size.x + 25), screenSize.y.coerceAtMost(size.y + 25))
        ImGui.setNextWindowSize(size.x, size.y)

        val pos = when (position.value) {
            ModuleListPosition.TOP_LEFT -> floatArrayOf(0f, 0f)
            ModuleListPosition.TOP_RIGHT -> floatArrayOf(screenSize.x - size.x, 0f)
            ModuleListPosition.BOTTOM_LEFT -> floatArrayOf(0f, screenSize.y - size.y)
            ModuleListPosition.BOTTOM_RIGHT -> floatArrayOf(screenSize.x - size.x, screenSize.y - size.y)
        }

        ImGui.setNextWindowPos(pos[0] + screenPos.x, pos[1] + screenPos.y)

        if (ImGui.begin("##Tangerine Module List", windowFlags)) {
            ImGui.setCursorPos(12.5f, 12.5f)
            ImGui.textUnformatted(moduleListString.toString())
        }

        ImGui.end()
    }

    @EventHandler
    fun onPostTick(event: TangerineEvent.PostTick) {
        val screen = Tangerine.mc.currentScreen
        shouldDraw = screen == null
    }

    enum class ModuleListPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    class ModuleListConfigWindow : ConfigWindow(ModuleListModule) {
        override fun drawConfig() {
            val new = GuiUtils.enumPicker(
                I18n.translate("tangerine.config.${position.group}.${position.name}"),
                position.value
            )

            if (new != position.value) position.set(new)
        }
    }
}
