package pm.n2.tangerine.modules;

import pm.n2.tangerine.modules.combat.CritsModule;
import pm.n2.tangerine.modules.misc.ModuleListModule;
import pm.n2.tangerine.modules.misc.UnifontModule;
import pm.n2.tangerine.modules.movement.*;
import pm.n2.tangerine.modules.player.AntiHungerModule;
import pm.n2.tangerine.modules.visuals.GlowESPModule;
import pm.n2.tangerine.modules.visuals.StorageESPModule;
import pm.n2.tangerine.modules.visuals.TracersModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	private void registerModule(Module module) {
		modules.add(module);
	}

	public void registerModules() {
		// movement
		registerModule(new FlightModule());
		registerModule(new NoFallModule());
		registerModule(new LiquidWalkModule());
		registerModule(new NoSlowModule());
		registerModule(new OmniSprintModule());
		registerModule(new BoatFlyModule());

		// combat
		registerModule(new CritsModule());

		// visuals
		registerModule(new GlowESPModule());
		registerModule(new StorageESPModule());
		registerModule(new TracersModule());

		// player
		registerModule(new AntiHungerModule());

		// automation

		// misc
		registerModule(new ModuleListModule());
		registerModule(new UnifontModule());
	}

	// FIXME: hack
	public <T> Module get(T module) {
		return modules.stream().filter(m -> m.getClass().equals(module)).findFirst().orElse(null);
	}

	public List<Module> getModules() {
		return this.modules;
	}

	public List<Module> getModulesByCategory(ModuleCategory category) {
		var modules = new ArrayList<Module>();

		for (Module module : this.modules) {
			if (module.category == category) {
				modules.add(module);
			}
		}

		return modules;
	}
}
