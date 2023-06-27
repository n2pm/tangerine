package pm.n2.tangerine

import com.mojang.blaze3d.platform.TextureUtil
import imgui.ImFontConfig
import imgui.ImGui
import net.minecraft.client.MinecraftClient
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents
import org.slf4j.LoggerFactory
import pm.n2.hajlib.event.EventManager
import pm.n2.hajlib.task.TaskManager
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.config.TangerineConfig
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.core.TangerineTaskContext
import pm.n2.tangerine.core.managers.CommandManager
import pm.n2.tangerine.core.managers.ModuleManager
import pm.n2.tangerine.core.managers.OverlayManager
import pm.n2.tangerine.gui.ImGuiManager
import pm.n2.tangerine.modules.misc.UnifontModule

object Tangerine : ClientModInitializer {
    const val modId = "tangerine"
    lateinit var version: String

    val logger = LoggerFactory.getLogger(modId)
    val eventManager = EventManager()
    val taskManager = TaskManager("Tangerine", TangerineTaskContext)

    lateinit var mc: MinecraftClient

    override fun onInitializeClient(mod: ModContainer) {
        mc = MinecraftClient.getInstance()
        version = mod.metadata().version().raw()

        ClientTickEvents.START.register(ClientTickEvents.Start {
            eventManager.dispatch(TangerineEvent.PreTick)
        })

        ClientTickEvents.END.register(ClientTickEvents.End {
            eventManager.dispatch(TangerineEvent.PostTick)
        })

        eventManager.registerFuncClass(TangerineEvent.KeyPress::class) {
            if (it !is TangerineEvent.KeyPress) return@registerFuncClass
            for (module in ModuleManager.items) {
                val configs = mutableListOf<ConfigOption<*>>(module.enabled)
                configs.addAll(module.configOptions)

                for (config in configs) {
                    if (config.keybind?.isJustPressed(it.key) == true) {
                        config.onKeybind()
                    }
                }
            }
        }

        CommandManager.init()
        ModuleManager.init()
        OverlayManager.init()

        ClientLifecycleEvents.STOPPING.register(ClientLifecycleEvents.Stopping {
            TangerineConfig.write()
        })

        doFonts()
    }

    private fun doFonts() {

        val useUnifont = UnifontModule.enabled.value
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

                ImGuiManager.setFont(if (useUnifont) ImGuiManager.fontUnifont else ImGuiManager.fontDefault)
            } else {
                logger.warn("Unifont font stream is null?")
            }
        } catch (e: Exception) {
            logger.error("Failed to load font", e)
        }
    }
}
