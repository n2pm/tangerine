package pm.n2.tangerine.commands

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.adryd.cauldron.api.command.ClientCommandManager
import com.adryd.cauldron.api.command.ClientCommandManager.argument
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.context.CommandContext

object ClipCommand : TangerineCommand {
    override fun register(dispatcher: CommandDispatcher<CauldronClientCommandSource>) {
        val command = ClientCommandManager
            .literal("clip")
            .then(argument("delta", IntegerArgumentType.integer())
                .executes { ctx -> this.run(ctx, getInteger(ctx, "delta")) })

        dispatcher.register(command)
    }

    private fun run(ctx: CommandContext<CauldronClientCommandSource>, delta: Int): Int {
        val player = ctx.source.player ?: return 0
        player.setPosition(player.pos.add(0.0, delta.toDouble(), 0.0))
        return 1
    }
}
