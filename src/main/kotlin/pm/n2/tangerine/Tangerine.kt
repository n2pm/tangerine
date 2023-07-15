package pm.n2.tangerine

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import pm.n2.hajlib.event.EventManager
import pm.n2.hajlib.imgui.ImGuiEvent
import pm.n2.hajlib.task.TaskManager
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.config.TangerineConfig
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.core.TangerineTaskContext
import pm.n2.tangerine.core.managers.CommandManager
import pm.n2.tangerine.core.managers.ModuleManager
import pm.n2.tangerine.core.managers.OverlayManager

object Tangerine : ClientModInitializer {
    const val modId = "tangerine"
    lateinit var version: String

    val logger = LoggerFactory.getLogger(modId)
    val eventManager = EventManager()
    val taskManager = TaskManager("Tangerine", TangerineTaskContext)

    lateinit var mc: MinecraftClient

    override fun onInitializeClient() {
        mc = MinecraftClient.getInstance()
        version = FabricLoader.getInstance().getModContainer(modId).get().metadata.version.toString()

        ClientTickEvents.START_CLIENT_TICK.register {
            eventManager.dispatch(TangerineEvent.PreTick)
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            eventManager.dispatch(TangerineEvent.PostTick)
        }

        eventManager.registerFunc(TangerineEvent.KeyPress::class) { event, _ ->
            var ret = false

            for (module in ModuleManager.items) {
                val configs = mutableListOf<ConfigOption<*>>(module.enabled)
                configs.addAll(module.configOptions)

                for (config in configs) {
                    if (config.keybind?.isJustPressed(event.key) == true) {
                        config.onKeybind()
                        ret = true

                        if (config == module.enabled) {
                            ModuleManager.toggle(module, true)
                        }
                    }
                }
            }

            return@registerFunc ret
        }

        CommandManager.init()
        ModuleManager.init()
        OverlayManager.init()

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            TangerineConfig.write()
        }

        pm.n2.hajlib.imgui.ImGuiManager.eventManager.registerFunc(ImGuiEvent.Draw::class) { _, _ ->
            eventManager.dispatch(TangerineEvent.ImGuiDraw)
        }
    }
}
