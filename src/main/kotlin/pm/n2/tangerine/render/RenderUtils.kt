package pm.n2.tangerine.render

import com.adryd.cauldron.api.util.Color4f
import java.awt.Color

object RenderUtils {
    val blue = Color(127, 127, 230, 255)
    val green = Color(127, 230, 127, 255)
    val yellow = Color(230, 230, 127, 255)
    val red = Color(230, 127, 127, 255)
    val white = Color(255, 255, 255, 255)
    val grey = Color(127, 127, 127, 255)
    val brown = Color(95, 70, 40, 255)
    val purple = Color(150, 100, 150, 255)
    val black = Color(0, 0, 0, 255)

    fun colorToCauldron(color: Color): Color4f =
        Color4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
}
