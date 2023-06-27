package pm.n2.tangerine.modules.movement

import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object NoSlowModule : Module("no_slowdown", "No slowdown", "Prevents you from slowing down", ModuleCategory.MOVEMENT) {
    val affectSneaking = BooleanConfigOption(id, "affect_sneaking", false)
    override val configOptions = listOf(affectSneaking)
}
