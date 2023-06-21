package pm.n2.tangerine.commands

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.adryd.cauldron.api.command.ClientCommandManager
import com.adryd.cauldron.api.command.ClientCommandManager.argument
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

class CarpetTPCommand : TangerineCommand {
    override fun register(dispatcher: CommandDispatcher<CauldronClientCommandSource>) {
        val command = ClientCommandManager
            .literal("carpettp")
            .then(
                argument("x", DoubleArgumentType.doubleArg())
                    .then(
                        argument("y", DoubleArgumentType.doubleArg())
                            .then(argument("z", DoubleArgumentType.doubleArg())
                                .executes { ctx ->
                                    this.run(
                                        ctx,
                                        DoubleArgumentType.getDouble(ctx, "x"),
                                        DoubleArgumentType.getDouble(ctx, "y"),
                                        DoubleArgumentType.getDouble(ctx, "z")
                                    )
                                })
                    )
            )

        dispatcher.register(command)
    }

    private fun run(ctx: CommandContext<CauldronClientCommandSource>, x: Double, y: Double, z: Double): Int {
        val player = ctx.source.player ?: return 0
        val mc = MinecraftClient.getInstance().networkHandler!!
        val worldHeight = 320.0

        mc.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(player.x, worldHeight, player.z, true))
        mc.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, worldHeight, z, true))
        mc.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true))

        player.setPosition(x, y, z)

        return 1
    }
}
