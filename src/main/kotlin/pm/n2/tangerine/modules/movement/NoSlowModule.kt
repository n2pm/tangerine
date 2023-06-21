package pm.n2.tangerine.modules.movement

import com.adryd.cauldron.api.config.ConfigBoolean
import com.adryd.cauldron.api.config.IConfigOption
import com.google.common.collect.ImmutableList
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

class NoSlowModule : Module("no_slowdown", "No slowdown", "Prevents you from slowing down", ModuleCategory.MOVEMENT) {
    val affectSneaking = ConfigBoolean("no_slowdown.affect_sneaking", false)
    override fun getConfigOptions(): ImmutableList<IConfigOption> = ImmutableList.of(affectSneaking)
}
