package pm.n2.tangerine.modules.visuals

import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object TracersModule : Module("tracers", "Tracers", "Draws lines towards entities", ModuleCategory.VISUALS) {
    val drawPlayers = BooleanConfigOption(id, "players", true)
    val drawFriendly = BooleanConfigOption(id, "friendly", true)
    val drawPassive = BooleanConfigOption(id, "passive", true)
    val drawHostile = BooleanConfigOption(id, "hostile", true)
    val drawItems = BooleanConfigOption(id, "items", false)
    val drawOthers = BooleanConfigOption(id, "others", false)
    val drawStem = BooleanConfigOption(id, "stem", true)

    override val configOptions = listOf(
        drawPlayers,
        drawFriendly,
        drawPassive,
        drawHostile,
        drawItems,
        drawOthers,
        drawStem
    )
}
