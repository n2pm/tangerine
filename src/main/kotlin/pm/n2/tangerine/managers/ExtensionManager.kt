package pm.n2.tangerine.managers

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.extension.Extension
import pm.n2.tangerine.extension.TangerineEntrypoint

object ExtensionManager: Manager {
    private val logger = LoggerFactory.getLogger("Tangerine ExtensionManager")
    val extensions =  findExtensions().toMutableList()

    override fun init() {
        logger.debug("Found ${extensions.size} extensions")

        extensions.forEach {
            it.entrypoint.onInitialize()
        }
    }

    fun findExtensions(): List<Extension> {
        val mods = FabricLoader.getInstance().getEntrypointContainers("tangerine", TangerineEntrypoint::class.java)
        return mods.map { Extension(it.provider, it.entrypoint) }
    }
}
