package pm.n2.tangerine.managers

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import pm.n2.hajlib.imgui.ImGuiEvent
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.config.TangerineConfig
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.core.TangerineEvent

object EventManager : Manager {
    override fun init() {
        ticks()
        input()
        gui()
        config()
    }

    private fun ticks() {
        ClientTickEvents.START_CLIENT_TICK.register {
            Tangerine.eventManager.dispatch(TangerineEvent.PreTick)
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            Tangerine.eventManager.dispatch(TangerineEvent.PostTick)
        }

    }

    private fun input() {
        Tangerine.eventManager.registerFunc(TangerineEvent.KeyPress::class) { event, _ ->
            var ret = false

            for (module in ModuleManager.modules) {
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
    }

    private fun gui() {
        pm.n2.hajlib.imgui.ImGuiManager.eventManager.registerFunc(ImGuiEvent.Draw::class) { _, _ ->
            Tangerine.eventManager.dispatch(TangerineEvent.ImGuiDraw)
        }
    }

    private fun config() {
        ClientLifecycleEvents.CLIENT_STOPPING.register {
            TangerineConfig.write()
        }
    }
}
