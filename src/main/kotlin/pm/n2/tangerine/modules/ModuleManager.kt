package pm.n2.tangerine.modules

import pm.n2.tangerine.modules.combat.CritsModule
import pm.n2.tangerine.modules.misc.ModuleListModule
import pm.n2.tangerine.modules.misc.UnifontModule
import pm.n2.tangerine.modules.movement.*
import pm.n2.tangerine.modules.player.AntiHungerModule
import pm.n2.tangerine.modules.visuals.GlowESPModule
import pm.n2.tangerine.modules.visuals.StorageESPModule
import pm.n2.tangerine.modules.visuals.TracersModule
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ModuleManager {
    val modules = mutableListOf(
            CritsModule(),

            ModuleListModule(),
            UnifontModule(),

            BoatFlyModule(),
            FlightModule(),
            LiquidWalkModule(),
            NoFallModule(),
            NoSlowModule(),
            OmniSprintModule(),

            AntiHungerModule(),

            GlowESPModule(),
            StorageESPModule(),
            TracersModule()
    )

    fun <T : Module> get(module: KClass<T>): T = modules.find { it::class == module } as T
    fun <T : Module> get(module: Class<T>): T = modules.find { it::class.java == module } as T

    fun getModulesByCategory(category: ModuleCategory): List<Module> {
        return modules.filter { it.category == category }
    }
}
