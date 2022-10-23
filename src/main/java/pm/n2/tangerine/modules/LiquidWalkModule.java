package pm.n2.tangerine.modules;

import pm.n2.tangerine.Module;
import pm.n2.tangerine.ModuleCategory;

public class LiquidWalkModule extends Module {
	public LiquidWalkModule() {
		super("Walk on liquids", "Treats water and lava like solid blocks", ModuleCategory.MOVEMENT);
	}
}
