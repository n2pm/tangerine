package pm.n2.tangerine.commands

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.adryd.cauldron.api.command.ClientCommandManager
import com.mojang.brigadier.CommandDispatcher
import pm.n2.tangerine.gui.renderables.PacketLoggerWindow

object PacketsCommand : TangerineCommand {
    override fun register(dispatcher: CommandDispatcher<CauldronClientCommandSource>) {
        val command = ClientCommandManager
            .literal("packets")
                .executes {
                    PacketLoggerWindow.enabled = !PacketLoggerWindow.enabled
                    return@executes 1
                }

        dispatcher.register(command)
    }
}
