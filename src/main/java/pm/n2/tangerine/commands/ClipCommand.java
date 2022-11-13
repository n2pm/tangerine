package pm.n2.tangerine.commands;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import com.adryd.cauldron.api.command.ClientCommandManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

public class ClipCommand {
	public static int run(CommandContext<CauldronClientCommandSource> ctx, int delta) {
		var player = ctx.getSource().getPlayer();
		if (player != null) {
			var playerPos = player.getPos();
			player.setPosition(playerPos.add(0, delta, 0));
			return 1;
		} else {
			return 0;
		}
	}

	public static void register(CommandDispatcher<CauldronClientCommandSource> dispatcher) {
		var command = ClientCommandManager
				.literal("clip")
				.then(ClientCommandManager.argument("delta", IntegerArgumentType.integer())
						.executes(ctx -> run(ctx, getInteger(ctx, "delta"))));

		dispatcher.register(command);
	}
}
