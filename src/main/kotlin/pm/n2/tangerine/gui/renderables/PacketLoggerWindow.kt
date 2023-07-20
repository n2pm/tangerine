package pm.n2.tangerine.gui.renderables

import imgui.ImGui
import imgui.flag.ImGuiSelectableFlags
import imgui.flag.ImGuiTableFlags
import imgui.type.ImBoolean
import net.minecraft.client.resource.language.I18n
import net.minecraft.util.Identifier
import pm.n2.tangerine.gui.TangerineRenderable
import pm.n2.tangerine.managers.ModuleManager
import pm.n2.tangerine.modules.misc.PacketLoggerModule
import java.lang.reflect.Field
import java.text.SimpleDateFormat


object PacketLoggerWindow : TangerineRenderable("PacketLoggerWindow", false) {
    var selectedPacket: PacketLoggerModule.TangerinePacket? = null

    override fun draw() {
        val enabled = ImBoolean(this.enabled)

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

        ImGui.beginTable("##PacketLoggerTable", 3, flags)

        ImGui.tableHeadersRow()

        ImGui.tableSetColumnIndex(0)
        ImGui.tableHeader(I18n.translate("tangerine.ui.packet_logger.table.direction"))

        ImGui.tableSetColumnIndex(1)
        ImGui.tableHeader(I18n.translate("tangerine.ui.packet_logger.table.name"))

        ImGui.tableSetColumnIndex(2)
        ImGui.tableHeader(I18n.translate("tangerine.ui.packet_logger.table.time"))

        val packets = PacketLoggerModule.packets.toList()
        for (packet in packets) {
            ImGui.tableNextRow()
            ImGui.tableNextColumn()

            val direction = if (packet.clientbound) "S2C" else "C2S"
            if (ImGui.selectable(
                    "$direction##${packet.packet}",
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

        // Declared fields + superclass fields
        for (field in recursiveFields(obj::class.java)) drawField(field, obj)
    }

    private fun recursiveFields(clazz: Class<*>): List<Field> {
        val fields = clazz.declaredFields.toMutableList()
        if (clazz.superclass != null) fields.addAll(recursiveFields(clazz.superclass))
        return fields
    }

    private fun drawField(field: Field, obj: Any?) {
        field.isAccessible = true
        val type = field.type.simpleName
        val name = field.name
        val value = field.get(obj)

        if (canBeToStringed(field.type)) {
            ImGui.textUnformatted("$name ($type): $value")
        } else {
            if (ImGui.treeNode("$name ($type)")) {
                drawClass(value)
            }
        }
    }

    private fun canBeToStringed(type: Class<*>) = type.isPrimitive
            || type == String::class.java
            || type == Identifier::class.java
}

