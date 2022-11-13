package pm.n2.tangerine.modules.movement;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.IConfigOption;
import com.google.common.collect.ImmutableList;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;

public class NoSlowModule extends Module {
	public ConfigBoolean affectSneaking = new ConfigBoolean("no_slowdown_affect_sneaking", "Affect sneaking", false);

	public NoSlowModule() { super("no_slowdown", "No slowdown", "Prevents you from slowing down", ModuleCategory.MOVEMENT); }

	@Override
	public ImmutableList<IConfigOption> getConfigOptions() {
		return ImmutableList.of(affectSneaking);
	}
}
