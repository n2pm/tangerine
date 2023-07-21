package pm.n2.tangerine.commands

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.mojang.brigadier.CommandDispatcher

interface TangerineCommand {
    fun register(dispatcher: CommandDispatcher<CauldronClientCommandSource>)
}
