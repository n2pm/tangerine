package pm.n2.tangerine.modules.visuals;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.IConfigOption;
import com.adryd.cauldron.api.render.helper.OverlayRenderManager;
import com.google.common.collect.ImmutableList;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;
import pm.n2.tangerine.render.OverlayTracers;

public class TracersModule extends Module {
	public ConfigBoolean drawPlayers = new ConfigBoolean("tracers.players", true);
	public ConfigBoolean drawFriendly = new ConfigBoolean("tracers.friendly", true);
	public ConfigBoolean drawPassive = new ConfigBoolean("tracers.passive", true);
	public ConfigBoolean drawHostile = new ConfigBoolean("tracers.hostile", true);
	public ConfigBoolean drawItems = new ConfigBoolean("tracers.items", false);
	public ConfigBoolean drawOthers = new ConfigBoolean("tracers.others", false);

	public ConfigBoolean drawStem = new ConfigBoolean("tracers.stem", true);

	public TracersModule() {
		super("tracers", "Tracers", "Draws lines towards entities", ModuleCategory.VISUALS);
		OverlayRenderManager.addRenderer(new OverlayTracers());
	}

	@Override
	public ImmutableList<IConfigOption> getConfigOptions() {
		return ImmutableList.of(
				drawPlayers,
				drawFriendly,
				drawPassive,
				drawHostile,
				drawItems,
				drawOthers,

				drawStem
		);
	}
}
