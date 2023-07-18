package pm.n2.tangerine

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import pm.n2.hajlib.event.EventManager
import pm.n2.hajlib.task.TaskManager
import pm.n2.tangerine.core.TangerineTaskContext
import pm.n2.tangerine.managers.ManagerManager

object Tangerine : ClientModInitializer {
    const val modId = "tangerine"
    lateinit var version: String

    val logger = LoggerFactory.getLogger(modId)
    val eventManager = EventManager()
    val taskManager = TaskManager("Tangerine", TangerineTaskContext)

    lateinit var mc: MinecraftClient

    override fun onInitializeClient() {
        mc = MinecraftClient.getInstance()
        version = FabricLoader.getInstance().getModContainer(modId).get().metadata.version.toString()

        ManagerManager.init()
    }
}
