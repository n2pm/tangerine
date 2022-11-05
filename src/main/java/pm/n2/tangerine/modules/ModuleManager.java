package pm.n2.tangerine.modules;

import pm.n2.tangerine.modules.combat.*;
import pm.n2.tangerine.modules.player.*;
import pm.n2.tangerine.modules.movement.*;
import pm.n2.tangerine.modules.visuals.GlowESPModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	private void registerModule(Module module) {
		modules.add(module);
	}

	public void registerModules() {
		registerModule(new FlightModule());
		registerModule(new NoFallModule());
		registerModule(new LiquidWalkModule());
		registerModule(new NoSlowModule());

		registerModule(new GlowESPModule());

		registerModule(new CritsModule());

		registerModule(new AntiHungerModule());
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
