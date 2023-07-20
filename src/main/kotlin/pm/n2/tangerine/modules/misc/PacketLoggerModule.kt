package pm.n2.tangerine.modules.misc

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.packet.Packet
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object PacketLoggerModule : Module("packet_logger", ModuleCategory.MISC) {
    val packets = mutableListOf<TangerinePacket>()
    val names = mutableMapOf<Class<*>, String>()
    var captureStart = System.currentTimeMillis()

    override fun onEnabled() {
        super.onEnabled()
        captureStart = System.currentTimeMillis()
    }

    @EventHandler
    fun onC2S(event: TangerineEvent.C2SPacket) = packets.add(
        TangerinePacket(event.packet, getName(event.packet), System.currentTimeMillis(), false)
    )

    @EventHandler
    fun onS2C(event: TangerineEvent.S2CPacket) = packets.add(
        TangerinePacket(event.packet, getName(event.packet), System.currentTimeMillis(), true)
    )

    fun clearPackets() = packets.clear()

    private fun getName(packet: Packet<*>): String {
        if (names.containsKey(packet.javaClass)) return names[packet.javaClass]!!
        val className = packet.javaClass.name
        val fullName = FabricLoader.getInstance().mappingResolver.mapClassName("intermediary", className)

        // jank
        val regex = Regex("^net\\.minecraft\\.network\\.packet\\..+?\\..+?\\.(.+?)$")
        println(regex.pattern)
        println(fullName)
        println(regex.find(fullName))
        val match = regex.find(fullName) ?: return className
        val name = match.groupValues[1]

        names[packet.javaClass] = name
        return name
    }

    data class TangerinePacket(
        val packet: Packet<*>,
        val name: String,
        val sentAt: Long,
        val clientbound: Boolean
    )
}
