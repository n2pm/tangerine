package pm.n2.tangerine.extension

import pm.n2.tangerine.commands.TangerineCommand
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

abstract class TangerineEntrypoint {
    abstract fun onInitialize()

    open fun getModules(): List<Module> = listOf()

    open fun getCommands(): List<TangerineCommand> = listOf()

    open fun getCategories(): List<ModuleCategory> = listOf()
}
