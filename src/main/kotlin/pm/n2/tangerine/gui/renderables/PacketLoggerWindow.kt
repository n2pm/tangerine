package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.flag.ImGuiCond
import imgui.flag.ImGuiSelectableFlags
import imgui.flag.ImGuiTableFlags
import imgui.type.ImBoolean
import net.fabricmc.loader.api.MappingResolver
import net.minecraft.client.resource.language.I18n
import net.minecraft.network.NetworkSide
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.managers.MappingManager
import pm.n2.tangerine.managers.ModuleManager
import pm.n2.tangerine.modules.misc.PacketLoggerModule
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.*

object PacketLoggerWindow : TangerineRenderable("PacketLoggerWindow", false) {
    var selectedPacket: PacketLoggerModule.TangerinePacket? = null

    override fun draw() {
        val enabled = ImBoolean(this.enabled)

        ImGui.setNextWindowSize(500f, 250f, ImGuiCond.FirstUseEver)
        if (ImGui.begin(I18n.translate("tangerine.ui.packet_logger.name"), enabled)) {
            drawMain()
            drawPacket()
        }

        ImGui.end()
        this.enabled = enabled.get()
    }

    private fun drawMain() {
        ImGui.sameLine()
        val cra = ImGui.getContentRegionAvail()
        ImGui.beginChild("##PacketLoggerMain", cra.x / 2, cra.y, true)

        val enableButton = I18n.translate(
            if (PacketLoggerModule.enabled.value) "tangerine.ui.disable" else "tangerine.ui.enable"
        )

        if (ImGui.button(enableButton)) ModuleManager.toggle(PacketLoggerModule)
        ImGui.sameLine()
        if (ImGui.button(I18n.translate("tangerine.ui.clear"))) PacketLoggerModule.clearPackets()

        val flags = (ImGuiTableFlags.Borders
            or ImGuiTableFlags.NoSavedSettings
            or ImGuiTableFlags.RowBg
            or ImGuiTableFlags.Resizable
            or ImGuiTableFlags.ScrollX
            or ImGuiTableFlags.ScrollY)

        if (!ImGui.beginTable("##PacketLoggerTable", 3, flags)) {
            ImGui.endChild()
            return
        }

        ImGui.tableHeadersRow()

        ImGui.tableSetColumnIndex(0)
        ImGui.tableHeader(I18n.translate("tangerine.ui.packet_logger.table.direction"))

        ImGui.tableSetColumnIndex(1)
        ImGui.tableHeader(I18n.translate("tangerine.ui.packet_logger.table.name"))

        ImGui.tableSetColumnIndex(2)
        ImGui.tableHeader(I18n.translate("tangerine.ui.packet_logger.table.time"))

        val packets = PacketLoggerModule.packets.toList().reversed()
        for (packet in packets) {
            ImGui.tableNextRow()
            ImGui.tableNextColumn()

            val direction = if (packet.side == NetworkSide.CLIENTBOUND) "S2C" else "C2S"
            val packetID = packet.packet.hashCode().toString() + "-" + packet.side + "-" + packet.sentAt
            if (ImGui.selectable(
                    "$direction##$packetID",
                    selectedPacket == packet,
                    ImGuiSelectableFlags.SpanAllColumns
                )
            ) {
                selectedPacket = packet
            }
            ImGui.tableNextColumn()

            ImGui.textUnformatted(packet.name)
            ImGui.tableNextColumn()

            val dateStr = SimpleDateFormat("HH:mm:ss.SSS").format(packet.sentAt - PacketLoggerModule.captureStart)
            ImGui.textUnformatted(dateStr)
        }

        ImGui.endTable()
        ImGui.endChild()
    }

    private fun drawPacket() {
        ImGui.sameLine()
        val cra = ImGui.getContentRegionAvail()
        ImGui.beginChild("##PacketLoggerPacket", cra.x, cra.y, true)

        if (selectedPacket == null) {
            ImGui.endChild()
            return
        }

        drawClass(selectedPacket!!.packet)
        ImGui.endChild()
    }

    private fun drawClass(obj: Any?) {
        if (obj == null) {
            ImGui.textUnformatted("null")
            return
        }

        // if it's a java list
        if (obj is List<*>) {
            for (i in obj.indices) {
                if (ImGui.treeNode("[$i]")) {
                    drawClass(obj[i])
                    ImGui.treePop()
                }
            }
            return
        }

        // Declared fields + superclass fields
        for (field in recursiveFields(obj::class.java)) {
            try {
                drawField(obj::class.java, field, obj)
            } catch (e: Exception) {
                // ignored
            }
        }
    }

    private fun recursiveFields(clazz: Class<*>): List<Field> {
        val fields = clazz.declaredFields.toMutableList()
        if (clazz.superclass != null) fields.addAll(recursiveFields(clazz.superclass))
        return fields
    }

    private fun drawField(clazz: Class<*>, field: Field, obj: Any?) {
        if (Modifier.isStatic(field.modifiers)) return

        field.isAccessible = true
        val type = MappingManager.mapClassSimple(field.type)
        val name = MappingManager.mapField(clazz, field)
        val value = field.get(obj)

        if (canBeToStringed(field.type)) {
            val valueStr = stringify(value)
            ImGui.textUnformatted("$name ($type): $valueStr")
        } else {
            if (ImGui.treeNode("$name ($type)")) {
                drawClass(value)
                ImGui.treePop()
            }
        }
    }

    private fun stringify(value: Any): String {
        if (value is Text) return value.string

        return value.toString()
    }

    private fun canBeToStringed(type: Class<*>) = type.isPrimitive
        || type.isEnum
        || type == String::class.java
        || type == Identifier::class.java
        || type == UUID::class.java
        || type.isAssignableFrom(Text::class.java)
}

