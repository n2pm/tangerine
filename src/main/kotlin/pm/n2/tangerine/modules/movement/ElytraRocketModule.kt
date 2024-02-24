package pm.n2.tangerine.modules.movement

import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import pm.n2.tangerine.config.BooleanConfigOption
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

// <Emma> you stole my mod
// <notnite> yeah i did just blatantly but thats because its a very cool idea
// <Emma> please pay royalties
// <Emma> 200 notcoin
object ElytraRocketModule : Module("elytra_rocket", ModuleCategory.MOVEMENT) {
    private val requireRocket = BooleanConfigOption(id, "require_rocket", true)
    override val configOptions = listOf(requireRocket)

    override fun init() {
        UseItemCallback.EVENT.register { player, world, hand ->
            val item = player.getStackInHand(hand)
            if (!this.enabled.value) return@register TypedActionResult.pass(item)
            if (player !is ClientPlayerEntity) return@register TypedActionResult.pass(item)
            if (!player.isFallFlying) return@register TypedActionResult.pass(item)

            if (this.requireRocket.value) {
                val registryKey = item.registryEntry.key
                if (!registryKey.isPresent) return@register TypedActionResult.pass(item)
                if (registryKey.get().value != Identifier("minecraft:firework_rocket"))
                    return@register TypedActionResult.pass(item)
            }

            // Stolen from FireworkRocketEntity
            val vec3d = player.getRotationVector()
            val vec3d2 = player.getVelocity()
            player.setVelocity(
                vec3d2.add(
                    vec3d.x * 0.1 + (vec3d.x * 1.5 - vec3d2.x) * 0.5,
                    vec3d.y * 0.1 + (vec3d.y * 1.5 - vec3d2.y) * 0.5,
                    vec3d.z * 0.1 + (vec3d.z * 1.5 - vec3d2.z) * 0.5
                )
            )

            return@register TypedActionResult.fail(item)
        }
    }
}
