package pm.n2.tangerine.commands

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.adryd.cauldron.api.command.ClientCommandManager
import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.delay
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.gui.TangerineScreen
import pm.n2.tangerine.gui.renderables.PacketLoggerWindow

object PacketsCommand : TangerineCommand {
    override fun register(dispatcher: CommandDispatcher<CauldronClientCommandSource>) {
        val command = ClientCommandManager
            .literal("packets")
            .executes {
                PacketLoggerWindow.enabled = !PacketLoggerWindow.enabled

                if (PacketLoggerWindow.enabled) {
                    // jank way to run when chat screen is cleared
                    Tangerine.taskManager.run {
                        delay(100)
                        Tangerine.mc.execute {
                            Tangerine.mc.setScreen(TangerineScreen)
                        }
                    }
                }

                return@executes 1
            }

        dispatcher.register(command)
    }
}
