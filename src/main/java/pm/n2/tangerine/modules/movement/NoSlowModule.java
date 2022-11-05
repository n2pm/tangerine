package pm.n2.tangerine.modules.movement;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigOptionBase;
import com.google.common.collect.ImmutableList;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class NoSlowModule extends Module {
	public ConfigBoolean affectSneaking = new ConfigBoolean("Affect sneaking", false);

	public NoSlowModule() { super("No Slowdown", "Prevents you from slowing down", ModuleCategory.MOVEMENT); }

	@Override
	public ImmutableList<ConfigOptionBase> getConfigOptions() {
		return ImmutableList.of(affectSneaking);
	}
}
