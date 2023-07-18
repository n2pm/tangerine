package pm.n2.tangerine.commands

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.adryd.cauldron.api.command.ClientCommandManager
import com.adryd.cauldron.api.command.ClientCommandManager.argument
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.util.math.Vec3d
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.commands.arguments.pos.CauldronVec3ArgumentType
import pm.n2.tangerine.managers.ClipManager

object PaperTPCommand : TangerineCommand {
    override fun register(dispatcher: CommandDispatcher<CauldronClientCommandSource>) {
        val command = ClientCommandManager
            .literal("papertp")
            .then(argument("pos", CauldronVec3ArgumentType.vec3())
                .executes { ctx -> this.run(ctx, CauldronVec3ArgumentType.getVec3(ctx, "pos")) })

        dispatcher.register(command)
    }

    private fun run(ctx: CommandContext<CauldronClientCommandSource>, target: Vec3d): Int {
        Tangerine.taskManager.run { ClipManager.execute(ctx.source.player.pos, target, it) }

        return 1
    }
}
