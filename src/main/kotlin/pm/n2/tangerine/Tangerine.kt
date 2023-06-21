package pm.n2.tangerine

import com.adryd.cauldron.api.config.ConfigFile
import com.mojang.blaze3d.platform.TextureUtil
import imgui.ImFont
import imgui.ImFontConfig
import imgui.ImGui
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents
import org.slf4j.LoggerFactory
import pm.n2.tangerine.commands.CommandManager
import pm.n2.tangerine.gui.ImGuiManager
import pm.n2.tangerine.gui.ImGuiScreen
import pm.n2.tangerine.gui.renderables.AboutWindow
import pm.n2.tangerine.gui.renderables.DemoWindow
import pm.n2.tangerine.gui.renderables.MenuBar
import pm.n2.tangerine.modules.ModuleManager
import pm.n2.tangerine.modules.misc.UnifontModule

public object Tangerine : ClientModInitializer {
    const val modId = "tangerine"
    lateinit var version: String

    val logger = LoggerFactory.getLogger(modId)
    val config = ConfigFile(modId)

    val imguiManager = ImGuiManager()
    val imguiScreen = ImGuiScreen()
    lateinit var imguiFontDefault: ImFont
    var imguiFontUnifont: ImFont? = null

    val moduleManager = ModuleManager()
    val commandManager = CommandManager()
    val keyboardManager = KeyboardManager()

    override fun onInitializeClient(mod: ModContainer) {
        version = mod.metadata().version().raw()

        imguiManager.addRenderable(MenuBar())
        imguiManager.addRenderable(DemoWindow())
        imguiManager.addRenderable(AboutWindow())

        commandManager.registerCommands()

        ClientTickEvents.START.register(ClientTickEvents.Start {
            for (module in moduleManager.modules) module.onStartTick(it)
        })

        ClientTickEvents.END.register(ClientTickEvents.End {
            for (module in moduleManager.modules) module.onEndTick(it)
        })

        keyboardManager.keyPress.register(KeyboardManager.KeyPress {
            for (module in moduleManager.modules) {
                val value = module.keybind.integerValue
                if (value != 0 && value == it) module.enabled.toggle()
            }
        })

        for (module in moduleManager.modules) {
            config.addConfig(module.enabled)
            config.addConfig(module.keybind)
            config.addConfigs(module.getConfigOptions())
        }

        ClientLifecycleEvents.STOPPING.register(ClientLifecycleEvents.Stopping {
            config.write()
        })

        val useUnifont = moduleManager.get(UnifontModule::class).enabled.booleanValue
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
                imguiFontDefault = fonts.addFontDefault(fontConfig)
                imguiFontUnifont = fonts.addFontFromMemoryTTF(arr, 16f, fontConfig)

                fonts.build()
                fontConfig.destroy()

                imguiManager.setFont(if (useUnifont) imguiFontUnifont!! else imguiFontDefault)
            } else {
                logger.warn("Unifont font stream is null?")
            }
        } catch (e: Exception) {
            logger.error("Failed to load font", e)
        }
    }
}
