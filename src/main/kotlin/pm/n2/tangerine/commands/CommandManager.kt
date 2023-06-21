package pm.n2.tangerine.commands

import com.adryd.cauldron.api.command.ClientCommandManager

class CommandManager {
    private val commands = listOf(ClipCommand())

    fun registerCommands() {
        commands.forEach { cmd -> cmd.register(ClientCommandManager.DISPATCHER) }
    }
}
