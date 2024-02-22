package pm.n2.tangerine.managers

import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.config.ConfigOption
import pm.n2.tangerine.config.TangerineConfig
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import pm.n2.tangerine.modules.combat.CritsModule
import pm.n2.tangerine.modules.combat.KillAuraModule
import pm.n2.tangerine.modules.misc.*
import pm.n2.tangerine.modules.movement.*
import pm.n2.tangerine.modules.player.AntiHungerModule
import pm.n2.tangerine.modules.visuals.BlockESPModule
import pm.n2.tangerine.modules.visuals.GlowESPModule
import pm.n2.tangerine.modules.visuals.StorageESPModule
import pm.n2.tangerine.modules.visuals.TracersModule

object ModuleManager : Manager {
    val categories = mutableListOf(
        ModuleCategory.MOVEMENT,
        ModuleCategory.PLAYER,
        ModuleCategory.COMBAT,
        ModuleCategory.MISC,
        ModuleCategory.VISUALS
    )

    val modules = mutableListOf(
        CritsModule,
        KillAuraModule,

        ModuleListModule,
        GUIModule,
        FastBreakModule,
        CivBreakModule,
        PacketLoggerModule,

        BoatFlyModule,
        FlightModule,
        IgnoreRotationModule,
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
        // Let extensions add their modules before doing our magic
        ExtensionManager.extensions.forEach { categories.addAll(it.entrypoint.getCategories()) }
        ExtensionManager.extensions.forEach { modules.addAll(it.entrypoint.getModules()) }

        // Setup config
        for (module in modules) {
            val options = mutableListOf<ConfigOption<*>>(module.enabled)
            options.addAll(module.configOptions)
            TangerineConfig.addConfigOptions(options)
        }

        TangerineConfig.read()

        // Actual init code
        for (module in modules) {
            module.init()
            if (module.enabled.value) Tangerine.eventManager.registerClass(module)
        }
    }

    fun toggle(module: Module, skip: Boolean = false) {
        if (!skip) module.enabled.toggle()

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
        return modules.filter { it.category == category }
    }
}
