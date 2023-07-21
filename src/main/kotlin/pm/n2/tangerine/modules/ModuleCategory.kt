package pm.n2.tangerine.modules

class ModuleCategory(val id: String) {
    companion object {
        val MOVEMENT = ModuleCategory("movement")
        val VISUALS = ModuleCategory("visuals")
        val PLAYER = ModuleCategory("player")
        val COMBAT = ModuleCategory("combat")
        val MISC = ModuleCategory("misc")
    }
}
