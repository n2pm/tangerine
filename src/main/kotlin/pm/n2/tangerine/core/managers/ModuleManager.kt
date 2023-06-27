package pm.n2.tangerine.core.managers

import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.config.TangerineConfig
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.modules.combat.CritsModule
import pm.n2.tangerine.modules.combat.KillAuraModule
import pm.n2.tangerine.modules.misc.ModuleListModule
import pm.n2.tangerine.modules.misc.UnifontModule
import pm.n2.tangerine.modules.movement.*
import pm.n2.tangerine.modules.player.AntiHungerModule
import pm.n2.tangerine.modules.visuals.BlockESPModule
import pm.n2.tangerine.modules.visuals.GlowESPModule
import pm.n2.tangerine.modules.visuals.StorageESPModule
import pm.n2.tangerine.modules.visuals.TracersModule

object ModuleManager : Manager<Module>() {
    override val items = listOf(
        CritsModule,
        KillAuraModule,

        ModuleListModule,
        UnifontModule,

        BoatFlyModule,
        FlightModule,
        LiquidWalkModule,
        NoFallModule,
        NoSlowModule,
        OmniSprintModule,

        AntiHungerModule,

        BlockESPModule,
        GlowESPModule,
        StorageESPModule,
        TracersModule
    )

    override fun init() {
        // Setup config
        for (module in items) {
            val options = mutableListOf<ConfigOption<*>>(module.enabled)
            options.addAll(module.configOptions)
            TangerineConfig.addConfigOptions(options)
        }

        TangerineConfig.read()

        // Actual init code
        for (module in items) {
            if (module.enabled.value) {
                Tangerine.eventManager.registerClass(module)
            }
        }
    }

    fun toggle(module: Module) {
        module.enabled.toggle()

        if (module.enabled.value) {
            Tangerine.eventManager.registerClass(module)
            module.onEnabled()
        } else {
            Tangerine.eventManager.unregisterClass(module)
            module.onDisabled()
        }

        TangerineConfig.write()
    }

    fun getModulesByCategory(category: ModuleCategory): List<Module> {
        return items.filter { it.category == category }
    }
}
