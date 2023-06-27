package pm.n2.tangerine.gui

import imgui.ImGui
import java.awt.Color

object GuiUtils {
    fun colorPicker(text: String, col: Color): Color? {
        val arr = floatArrayOf(
            col.red.toFloat() / 255f,
            col.green.toFloat() / 255f,
            col.blue.toFloat() / 255f,
            col.alpha.toFloat() / 255f
        )

        if (ImGui.colorEdit4(text, arr)) {
            return Color(arr[0], arr[1], arr[2], arr[3])
        }

        return null
    }
}
