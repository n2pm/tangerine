package pm.n2.tangerine.modules.visuals

import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.config.ColorConfigOption
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.render.RenderUtils
import java.awt.Color

object TracersModule : Module("tracers", ModuleCategory.VISUALS) {
    val drawPlayers = BooleanConfigOption(id, "players", true)
    val drawFriendly = BooleanConfigOption(id, "friendly", true)
    val drawPassive = BooleanConfigOption(id, "passive", true)
    val drawHostile = BooleanConfigOption(id, "hostile", true)
    val drawItems = BooleanConfigOption(id, "items", false)
    val drawOthers = BooleanConfigOption(id, "others", false)
    val drawStem = BooleanConfigOption(id, "stem", true)

    val playersColor = ColorConfigOption(id, "players_color", RenderUtils.blue)
    val passiveColor = ColorConfigOption(id, "passive_color", RenderUtils.green)
    val angerableColor = ColorConfigOption(id, "angerable_color", RenderUtils.yellow)
    val hostileColor = ColorConfigOption(id, "hostile_color", RenderUtils.red)
    val itemColor = ColorConfigOption(id, "item_color", RenderUtils.white)
    val othersColor = ColorConfigOption(id, "others_color", RenderUtils.grey)

    override val configOptions = listOf(
        drawPlayers,
        drawFriendly,
        drawPassive,
        drawHostile,
        drawItems,
        drawOthers,
        drawStem,

        playersColor,
        passiveColor,
        angerableColor,
        hostileColor,
        itemColor,
        othersColor
    )
}
