package pm.n2.tangerine.config

import java.awt.Color

class ColorConfigOption(
    override val group: String,
    override val name: String,
    override var value: Color
) : ConfigOption<Color>(group, name, value)
