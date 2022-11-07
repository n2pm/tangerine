package pm.n2.tangerine.commands;

import com.adryd.cauldron.api.command.ClientCommandManager;

public class CommandManager {
	public void registerCommands() {
		ClipCommand.register(ClientCommandManager.DISPATCHER);
	}
}
