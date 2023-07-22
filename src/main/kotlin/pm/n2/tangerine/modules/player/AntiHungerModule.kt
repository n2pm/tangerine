package pm.n2.tangerine.modules.player

import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.modules.movement.FlightModule

object AntiHungerModule : Module("anti_hunger", ModuleCategory.PLAYER) {
    val turnedOn = BooleanConfigOption(id, "turned_on", false)
    override val configOptions = listOf(turnedOn)

    override fun onEnabled() {
        if (turnedOn.value) Tangerine.mc.networkHandler?.sendChatMessage("Turned on")
    }
}
