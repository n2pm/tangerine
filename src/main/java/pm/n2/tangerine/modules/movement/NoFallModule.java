package pm.n2.tangerine.modules.movement;

import pm.n2.tangerine.modules.ModuleCategory;
import pm.n2.tangerine.modules.Module;

public class NoFallModule extends Module {
	public NoFallModule() {
		super("No fall damage", "Prevents you from taking fall damage", ModuleCategory.MOVEMENT);
	}
}
