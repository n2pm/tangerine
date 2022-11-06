package pm.n2.tangerine.modules.visuals;

import com.adryd.cauldron.api.config.ConfigBoolean;
import com.adryd.cauldron.api.config.ConfigOptionBase;
import com.adryd.cauldron.api.config.IConfigOption;
import com.adryd.cauldron.api.render.helper.OverlayRenderManager;
import com.google.common.collect.ImmutableList;
import pm.n2.tangerine.modules.Module;
import pm.n2.tangerine.modules.ModuleCategory;
import pm.n2.tangerine.render.OverlayTracers;

public class TracersModule extends Module {
	public ConfigBoolean drawPlayers = new ConfigBoolean("tracers_players", "Draw players", true);
	public ConfigBoolean drawFriendly = new ConfigBoolean("tracers_friendly", "Draw friendly mobs", true);
	public ConfigBoolean drawPassive = new ConfigBoolean("tracers_passive", "Draw passive mobs", true);
	public ConfigBoolean drawHostile = new ConfigBoolean("tracers_hostile", "Draw hostile mobs", true);
	public ConfigBoolean drawItems = new ConfigBoolean("tracers_items", "Draw items", false);
	public ConfigBoolean drawOthers = new ConfigBoolean("tracers_others", "Draw other entities", false);

	public ConfigBoolean drawStem = new ConfigBoolean("tracers_stem", "Draw stem", true);

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
