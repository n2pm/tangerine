package pm.n2.tangerine.managers

import com.adryd.cauldron.api.command.ClientCommandManager
import pm.n2.tangerine.commands.*
import pm.n2.tangerine.core.Manager

object CommandManager : Manager {
    private val commands = mutableListOf(PaperTPCommand, CarpetTPCommand, ClipCommand, PacketsCommand)

    override fun init() {
        ExtensionManager.extensions.forEach { commands.addAll(it.entrypoint.getCommands()) }
        commands.forEach { cmd -> cmd.register(ClientCommandManager.DISPATCHER) }
    }
}
