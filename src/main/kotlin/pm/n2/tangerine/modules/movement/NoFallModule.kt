package pm.n2.tangerine.modules.movement

import net.minecraft.block.FluidBlock
import net.minecraft.fluid.Fluids
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.shape.VoxelShapes
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

object NoFallModule : Module("no_fall", ModuleCategory.MOVEMENT) {
    private var wasFlying = false
    private var isFlying = true
    private var ticks = 0

    @EventHandler
    fun onPreTick(event: TangerineEvent.PreTick) {
        val mc = Tangerine.mc
        if (mc.player != null && mc.world != null && enabled.value) {
            isFlying = mc.player!!.abilities.flying
        }
    }

    @EventHandler
    fun onPostTick(event: TangerineEvent.PostTick) {
        val mc = Tangerine.mc
        if (mc.player != null && mc.world != null && enabled.value) {
            if (mc.player!!.fallDistance > 3f) {
                val pos = mc.player!!.pos

                var blockPos = mc.player!!.blockPos
                blockPos = blockPos.add(0, (-1.5).toInt(), 0)
                blockPos = blockPos.add(0, (mc.player!!.velocity.getY() * 0.1).toInt(), 0)

                val blockState = mc.world!!.getBlockState(blockPos)
                val negateWater = LiquidWalkModule.enabled.value
                        && (blockState.block is FluidBlock || blockState.fluidState != Fluids.EMPTY.defaultState)

                if (blockState.getCollisionShape(
                        mc.world,
                        blockPos
                    ) !== VoxelShapes.empty() || negateWater || !wasFlying && isFlying || !isFlying
                ) {
                    mc.player!!.networkHandler.sendPacket(
                        PositionAndOnGround(
                            pos.getX(),
                            pos.getY() + 0.01f,
                            pos.getZ(),
                            false
                        )
                    )
                    mc.player!!.fallDistance = 0f
                }
            }

            ticks++
            if (ticks % 2 == 0) {
                wasFlying = mc.player!!.abilities.flying
                ticks = 0
            }
        }
    }
}
