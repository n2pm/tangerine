package pm.n2.tangerine.modules.visuals

import com.adryd.cauldron.api.config.ConfigBoolean
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object TracersModule : Module("tracers", "Tracers", "Draws lines towards entities", ModuleCategory.VISUALS) {
    val drawPlayers = ConfigBoolean("tracers.players", true)
    val drawFriendly = ConfigBoolean("tracers.friendly", true)
    val drawPassive = ConfigBoolean("tracers.passive", true)
    val drawHostile = ConfigBoolean("tracers.hostile", true)
    val drawItems = ConfigBoolean("tracers.items", false)
    val drawOthers = ConfigBoolean("tracers.others", false)
    val drawStem = ConfigBoolean("tracers.stem", true)

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
