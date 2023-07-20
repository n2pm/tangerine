package pm.n2.tangerine.core

import net.minecraft.network.packet.Packet

sealed class TangerineEvent {
    data object PreTick : TangerineEvent()
    data object PostTick : TangerineEvent()

    data class C2SPacket(val packet: Packet<*>) : TangerineEvent()
    data class S2CPacket(val packet: Packet<*>) : TangerineEvent()

    data class KeyPress(val key: Int) : TangerineEvent()
    data object ImGuiDraw : TangerineEvent()
}
