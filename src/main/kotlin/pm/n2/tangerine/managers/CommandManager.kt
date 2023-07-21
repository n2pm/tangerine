package pm.n2.tangerine.managers

import com.adryd.cauldron.api.command.ClientCommandManager
import pm.n2.tangerine.commands.CarpetTPCommand
import pm.n2.tangerine.commands.ClipCommand
import pm.n2.tangerine.commands.PacketsCommand
import pm.n2.tangerine.commands.PaperTPCommand
import pm.n2.tangerine.core.Manager

object CommandManager : Manager {
    private val commands = mutableListOf(PaperTPCommand, CarpetTPCommand, ClipCommand, PacketsCommand)

    override fun init() {
        // lets add extensions commands
        ExtensionManager.extensions.forEach { commands.addAll(it.entrypoint.getCommands()) }

        commands.forEach { cmd -> cmd.register(ClientCommandManager.DISPATCHER) }
    }
}
