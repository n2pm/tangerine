package pm.n2.tangerine.modules.combat

import com.adryd.cauldron.api.config.ConfigBoolean
import com.adryd.cauldron.api.config.ConfigDouble
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.MathHelper
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory
import kotlin.math.sqrt

object KillAuraModule : Module(
    "kill_aura",
    "Kill aura",
    "Attacks enemies in range on your weapon's cooldown",
    ModuleCategory.COMBAT
) {
    val maxReach = ConfigDouble("kill_aura.max_reach", 5.0, 0.1, 10.0)
    val attackPlayers = ConfigBoolean("kill_aura.attack_players", false)
    val attackPassive = ConfigBoolean("kill_aura.attack_passive", false)
    val attackNeutral = ConfigBoolean("kill_aura.attack_neutral", false)
    val attackHostile = ConfigBoolean("kill_aura.attack_hostile", false)

    override val configOptions = listOf(maxReach, attackPlayers, attackPassive, attackNeutral, attackHostile)

    @EventHandler
    fun onPreTick(event: TangerineEvent.PreTick) {
        val mc = Tangerine.mc
        val player = mc.player ?: return
        val world = mc.world ?: return
        val interaction = mc.interactionManager ?: return
        val network = mc.networkHandler ?: return

        var target: Entity? = null
        var closest = Float.MAX_VALUE

        for (entity in world.entities) {
            if (entity == player) continue
            if (!entity.isAlive) continue
            if (entity is LivingEntity && entity.isBlocking) continue

            val shouldAttack = when (entity) {
                is PlayerEntity -> attackPlayers.booleanValue
                is PassiveEntity -> attackPassive.booleanValue
                is HostileEntity -> attackHostile.booleanValue
                else -> attackNeutral.booleanValue
            }
            if (!shouldAttack) continue

            val distance = entity.distanceTo(player)
            if (distance < closest) {
                closest = distance
                target = entity
            }
        }

        if (closest > maxReach.doubleValue) target = null

        if (target == null) return

        if (player.getAttackCooldownProgress(0f) == 1f) {
            // Entity.lookAt() but reimpled to only send packet
            val eyePos = EntityAnchor.EYES.positionAt(player)
            val d = target.x - eyePos.x
            val e = target.y - eyePos.y
            val f = target.z - eyePos.z
            val g = sqrt(d * d + f * f)
            val pitch = MathHelper.wrapDegrees((-(MathHelper.atan2(e, g) * 180.0f / Math.PI.toFloat())).toFloat())
            val yaw = MathHelper.wrapDegrees((MathHelper.atan2(f, d) * 180.0f / Math.PI.toFloat()).toFloat() - 90.0f)

            val oldPitch = player.pitch
            val oldYaw = player.yaw

            network.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, player.isOnGround))
            interaction.attackEntity(player, target)
            network.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(oldYaw, oldPitch, player.isOnGround))
        }
    }
}
