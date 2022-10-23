package pm.n2.tangerine.modules;

import pm.n2.tangerine.ModuleCategory;
import pm.n2.tangerine.Module;

public class NoFallModule extends Module {
	public NoFallModule() {
		super("No fall damage", "Prevents you from taking fall damage", ModuleCategory.MOVEMENT);
	}
}
