package pm.n2.tangerine.core.managers

import com.adryd.cauldron.api.command.ClientCommandManager
import pm.n2.tangerine.commands.CarpetTPCommand
import pm.n2.tangerine.commands.ClipCommand
import pm.n2.tangerine.commands.TangerineCommand
import pm.n2.tangerine.core.Manager

object CommandManager : Manager<TangerineCommand>() {
    override val items = listOf(ClipCommand(), CarpetTPCommand())

    init {
        items.forEach { cmd -> cmd.register(ClientCommandManager.DISPATCHER) }
    }
}
