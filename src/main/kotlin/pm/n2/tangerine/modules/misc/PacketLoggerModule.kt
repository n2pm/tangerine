package pm.n2.tangerine.modules.misc

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.minecraft.network.NetworkSide
import net.minecraft.network.packet.Packet
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.gui.renderables.PacketLoggerWindow
import pm.n2.tangerine.managers.MappingManager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object PacketLoggerModule : Module("packet_logger", ModuleCategory.MISC) {
    val packets = mutableListOf<TangerinePacket>()
    var captureStart = System.currentTimeMillis()
    private val packetMutex = Mutex() // concurrency my beloathed
    private val names = mutableMapOf<Class<out Packet<*>>, String>()

    override fun onEnabled() {
        super.onEnabled()
        captureStart = System.currentTimeMillis()
    }

    fun clearPackets() = Tangerine.taskManager.run {
        packetMutex.withLock {
            packets.clear()
        }
    }

    @EventHandler
    fun onC2S(event: TangerineEvent.C2SPacket) = Tangerine.taskManager.run {
        packetMutex.withLock {
            packets.add(
                TangerinePacket(
                    event.packet,
                    getName(event.packet),
                    System.currentTimeMillis(),
                    NetworkSide.SERVERBOUND
                )
            )
        }
    }

    @EventHandler
    fun onS2C(event: TangerineEvent.S2CPacket) = Tangerine.taskManager.run {
        packetMutex.withLock {
            packets.add(
                TangerinePacket(
                    event.packet,
                    getName(event.packet),
                    System.currentTimeMillis(),
                    NetworkSide.CLIENTBOUND
                )
            )
        }
    }

    private fun getName(packet: Packet<*>): String {
        if (names.containsKey(packet.javaClass)) return names[packet.javaClass]!!
        val fullName = MappingManager.mapClass(packet.javaClass)

        // jank
        val regex = Regex("^net\\.minecraft\\.network\\.packet\\..+?\\..+?\\.(.+?)$")
        val match = regex.find(fullName) ?: return packet.javaClass.name
        val name = match.groupValues[1]

        names[packet.javaClass] = name
        return name
    }

    override val shouldHideEnabled = true
    override fun showConfigWindow() {
        PacketLoggerWindow.enabled = true
    }

    data class TangerinePacket(
        val packet: Packet<*>,
        val name: String,
        val sentAt: Long,
        val side: NetworkSide
    )

}
