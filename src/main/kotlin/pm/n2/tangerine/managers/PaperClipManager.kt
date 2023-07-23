package pm.n2.tangerine.managers

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import org.slf4j.LoggerFactory
import pm.n2.hajlib.event.EventHandler
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.Manager
import pm.n2.tangerine.core.TangerineEvent
import pm.n2.tangerine.core.TangerineTaskContext
import pm.n2.tangerine.managers.PaperClipManager.build
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * This code is partly inspired by Ari (adryd325)'s fast travel code (with permission) - thank you!
 *
 * Implementations my own (NotNite), but she inspired the algorithm in [build] for determining what to do.
 */
object PaperClipManager : Manager {
    val logger = LoggerFactory.getLogger("Tangerine PaperClipManager")
    var isRunning = false
    var lagbackAttempts = 0

    const val MAX_MOVE_PER_PACKET = 10
    const val MAX_PACKETS_PER_TICK = 20
    const val MAX_DISTANCE_PER_TICK = 200.0
    const val MAX_LAGBACK_ATTEMPTS = 10
    const val MAX_CHUNKLOAD_ATTEMPTS = 10
    const val ARRIVAL_TOLERANCE = 0.1
    const val LAGBACK_TOLERANCE = 1
    const val SKY_INTERMEDIATE = 320.0 // our feet will be on 320, which is guaranteed to be air
    const val VOID_INTERMEDIATE = -64.0 - 2.0 // 2 for the player to not suffocate in bedrock

    @EventHandler
    fun onDisconnected(event: TangerineEvent.Disconnected) {
        isRunning = false
    }

    // List of lists - each entry in the first list should have all its packets sent on a single tick
    fun build(from: Vec3d, to: Vec3d): List<List<Packet<*>>>? {
        // If we're only going vertically, don't bother with hclip
        if (from.x == to.x && from.z == to.z) {
            logger.debug("[build] Attempting to build vclip")
            val vclip = vclip(from, to)

            if (vclip != null) {
                logger.debug("[build] vclip succeeded")
            } else {
                logger.debug("[build] vclip failed - build failure!")
            }

            return vclip
        }

        logger.debug("[build] Attempting to build hclip")
        val maybeHclip = hclip(from, to)
        if (maybeHclip != null) {
            logger.debug("[build] hclip succeeded")
            return maybeHclip
        }

        // We're definitely trying to hclip here, but we hit something on the way.
        // As said below, we can either add complex pathfinding, or just clip into sky/void and hclip.
        // We're gonna try resolving both routes and pick the one with less packets.
        logger.debug("[build] hclip failed - building bothclip")

        val skyClip = bothclip(from, to, SKY_INTERMEDIATE)
        if (skyClip != null) {
            logger.debug("[build] bothclip succeeded (skyclip)")
            return skyClip
        }
        /*val voidClip = bothclip(from, to, VOID_INTERMEDIATE)
        logger.debug(
            "[build] skyclip - null: {}, {} packets in {} ticks",
            skyClip == null,
            skyClip?.flatten()?.size,
            skyClip?.size
        )
        logger.debug(
            "[build] voidclip - null: {}, {} packets in {} ticks",
            voidClip == null,
            voidClip?.flatten()?.size,
            voidClip?.size
        )

        // Resolve the case where both resolved.
        if (skyClip != null && voidClip != null) {
            val skyPackets = skyClip.flatten().size
            val voidPackets = voidClip.flatten().size
            logger.debug("[build] Both resolved - skyPackets: {}, voidPackets: {}", skyPackets, voidPackets)

            return if (skyPackets < voidPackets) {
                logger.debug("[build] bothclip succeeded (both resolved, picked skyclip)")
                skyClip
            } else {
                logger.debug("[build] bothclip succeeded (both resolved, picked voidclip)")
                voidClip
            }
        }

        // Resolve the case where only one of them resolved.
        if (skyClip == null && voidClip != null) {
            logger.debug("[build] bothclip succeeded (only voidclip resolved)")
            return voidClip
        } else if (skyClip != null && voidClip == null) {
            logger.debug("[build] bothclip succeeded (only skyclip resolved)")
            return skyClip
        }*/

        // vclip wasn't possible, hclip wasn't possible, and bothclip wasn't possible. We can't do anything.
        logger.debug("[build] bothclip failed - build failure!")
        return null
    }

    suspend fun execute(from: Vec3d, to: Vec3d, ctx: TangerineTaskContext) {
        val player = Tangerine.mc.player!!
        val world = Tangerine.mc.world!!
        val network = Tangerine.mc.networkHandler!!
        var currentPos = player.pos

        isRunning = true
        player.setNoGravity(true)
        player.vehicle?.setNoGravity(true)

        player.sendMessage(Text.literal("Building packets..."))
        val chunks = build(from, to) ?: run {
            player.sendMessage(Text.literal("Failed to build packets.").styled {
                it.withColor(Formatting.RED)
            })
            isRunning = false
            player.setNoGravity(false)
            player.vehicle?.setNoGravity(false)
            lagbackAttempts = 0
            return
        }

        for (chunk in chunks) {
            for (immutablePacket in chunk) {
                if (!isRunning) return
                var packet = immutablePacket
                var isPos = packet is PlayerMoveC2SPacket.PositionAndOnGround || packet is VehicleMoveC2SPacket

                if (packet is VehicleMoveC2SPacket) {
                    // jank way to fill it in with current vehicle pos
                    if (packet.x == 0.0 && packet.y == 0.0 && packet.z == 0.0 && packet.yaw == 0f && packet.pitch == 0f) {
                        isPos = false
                        packet = buildVehicleMove(
                            currentPos.x,
                            currentPos.y,
                            currentPos.z,
                            packet.yaw,
                            packet.pitch
                        )
                    }
                }

                if (isPos) {
                    val packetPos = if (packet is PlayerMoveC2SPacket.PositionAndOnGround) {
                        Vec3d(packet.getX(currentPos.x), packet.getY(currentPos.y), packet.getZ(currentPos.z))
                    } else {
                        packet as VehicleMoveC2SPacket
                        Vec3d(packet.x, packet.y, packet.z)
                    }

                    val packetChunk = ChunkPos(
                        BlockPos(
                            floor(packetPos.x).toInt(),
                            floor(packetPos.y).toInt(),
                            floor(packetPos.z).toInt()
                        )
                    )

                    var waitedTicks = 0
                    while (!world.chunkManager.isChunkLoaded(
                            packetChunk.x,
                            packetChunk.z
                        ) && waitedTicks < MAX_CHUNKLOAD_ATTEMPTS
                    ) {
                        if (!isRunning) return
                        ctx.waitTicks(2)
                        waitedTicks++
                    }

                    currentPos = packetPos
                }

                val str = when (packet) {
                    is PlayerMoveC2SPacket.PositionAndOnGround -> {
                        val x = packet.getX(currentPos.x)
                        val y = packet.getY(currentPos.y)
                        val z = packet.getZ(currentPos.z)
                        "PositionAndOnGround{x=$x,y=$y,z=$z}"
                    }

                    is VehicleMoveC2SPacket -> "VehicleMoveC2SPacket{x=${packet.x},y=${packet.y},z=${packet.z}}"
                    is PlayerMoveC2SPacket.OnGroundOnly -> "OnGroundOnly{onGround=${packet.isOnGround}"

                    else -> "???"
                }

                logger.debug("[execute] Sending packet: {}", str)

                network.sendPacket(packet)

                if (isPos) {
                    if (player.hasVehicle()) {
                        player.vehicle!!.setPos(currentPos.x, currentPos.y, currentPos.z)
                    }

                    player.setPos(currentPos.x, currentPos.y, currentPos.z)
                }
            }

            val chunkIdx = chunks.indexOf(chunk)
            val packetsDone = chunks.slice(0 until chunkIdx).flatten().size
            val packetsAfter = chunks.slice(chunkIdx + 1 until chunks.size).flatten().size
            val totalPackets = chunks.flatten().size

            val percent = (packetsDone.toDouble() / totalPackets.toDouble()) * 100
            val percentStr = "%.2f".format(percent)

            val msg = "$packetsDone/$totalPackets - $packetsAfter left - $percentStr%"
            player.sendMessage(Text.literal(msg), true)

            ctx.waitTicks(5)
            if (player.pos.distanceTo(currentPos) > LAGBACK_TOLERANCE) {
                ctx.waitTicks(2)
                lagbackAttempts++

                if (lagbackAttempts > MAX_LAGBACK_ATTEMPTS) {
                    if (!isRunning) return
                    player.sendMessage(Text.literal("Lagged back too many times, aborting.").styled {
                        it.withColor(Formatting.RED)
                    })
                    isRunning = false
                    player.setNoGravity(false)
                    player.vehicle?.setNoGravity(false)
                    lagbackAttempts = 0
                    return
                }

                player.sendMessage(Text.literal("Lagged back, retrying..."))
                return execute(player.pos, to, ctx)
            }
        }

        isRunning = false
        player.setNoGravity(false)
        player.vehicle?.setNoGravity(false)
        lagbackAttempts = 0
        player.sendMessage(Text.literal("Done!"), true)
    }

    fun vclip(from: Vec3d, to: Vec3d): List<List<Packet<*>>>? {
        val packets = mutableListOf<List<Packet<*>>>()

        var lastPos = from
        while (true) {
            val (newPackets, newY) = vclipInternal(lastPos, to) ?: return null
            packets.add(newPackets)
            if (abs(newY - to.y) < ARRIVAL_TOLERANCE) break
            lastPos = Vec3d(lastPos.x, newY, lastPos.z)
        }

        logger.debug("[vclip] Resolved {} packets in {} ticks", packets.flatten().size, packets.size)
        return packets
    }

    fun hclip(from: Vec3d, to: Vec3d): List<List<Packet<*>>>? {
        val packets = mutableListOf<List<Packet<*>>>()

        var lastPos = from
        while (true) {
            val (newPackets, newPos) = hclipInternal(lastPos, to) ?: return null
            packets.add(newPackets)
            if (to.distanceTo(newPos) < ARRIVAL_TOLERANCE) break
            lastPos = newPos
        }

        logger.debug("[hclip] Resolved {} packets in {} ticks", packets.flatten().size, packets.size)
        return packets
    }

    fun bothclip(from: Vec3d, to: Vec3d, intermediate: Double): List<List<Packet<*>>>? {
        val packets = mutableListOf<List<Packet<*>>>()

        val fromIntermediate = Vec3d(from.x, intermediate, from.z)
        val toIntermediate = Vec3d(to.x, intermediate, to.z)

        logger.debug("[bothclip] Building vclip to intermediate - from: {}, to: {}", from, fromIntermediate)
        val vclipToIntermediate = vclip(from, fromIntermediate) ?: run {
            logger.debug("[bothclip] vclip to intermediate failed ({})", intermediate)
            return null
        }

        logger.debug("[bothclip] Building hclip to target - from: {}, to: {}", fromIntermediate, toIntermediate)
        val hclipToTarget = hclip(fromIntermediate, toIntermediate) ?: run {
            logger.debug("[bothclip] hclip to target failed ({})", intermediate)
            return null
        }

        logger.debug("[bothclip] Building vclip from intermediate - from: {}, to: {}", toIntermediate, to)
        val vclipFromIntermediate = vclip(toIntermediate, to) ?: run {
            logger.debug("[bothclip] vclip from intermediate failed ({})", intermediate)
            return null
        }

        // We can technically do some tick optimizations here, but /shrug
        packets.addAll(vclipToIntermediate)
        packets.addAll(hclipToTarget)
        packets.addAll(vclipFromIntermediate)

        logger.debug("[bothclip] Resolved {} packets in {} ticks", packets.flatten().size, packets.size)

        return packets
    }

    // TODO: what about entities? fluids? things that aren't air but we can fit in?
    // Ari has her own collision check but it's a little weird to use, I don't really want to port it rn
    private fun playerFits(pos: Vec3d): Boolean {
        val world = Tangerine.mc.world ?: return false
        val player = Tangerine.mc.player ?: return false

        val blockPos = BlockPos(floor(pos.x).toInt(), floor(pos.y).toInt(), floor(pos.z).toInt())
        val blockPosAbove = blockPos.up()

        val blockPosAir = world.getBlockState(blockPos).isAir
        val blockPosAboveAir = world.getBlockState(blockPosAbove).isAir

        logger.debug("[playerFits] pos: {}, blockPosAir: {}, blockPosAboveAir: {}", pos, blockPosAir, blockPosAboveAir)
        return blockPosAir && blockPosAboveAir
    }

    private fun vclipInternal(from: Vec3d, to: Vec3d): Pair<List<Packet<*>>, Double>? {
        val player = Tangerine.mc.player ?: return null

        val diff = abs(to.y - from.y)
        val stepCount = ceil(diff / MAX_MOVE_PER_PACKET).toInt()
        val step = diff / stepCount

        logger.debug("[vclipInternal] Diff: {}, step count: {}, step: {}", diff, stepCount, step)
        if (diff < ARRIVAL_TOLERANCE) {
            logger.debug("[vclipInternal] Uh... already here?")
            return Pair(listOf(), to.y)
        }

        // 1 more packet for the final position packet
        if (stepCount > (MAX_PACKETS_PER_TICK - 1)) {
            // We're not gonna make it this tick, let's try our best to find air and do more
            logger.debug("[vclipInternal] Not going to make it on this tick (MAX_PACKETS_PER_TICK) :(")

            val headedUp = to.y > from.y
            val max = 200 - 10 - 2 // this is fucking stupid

            val startPos = if (headedUp) from.y + max else from.y - max
            val endPos = from.y
            val length = abs(startPos - endPos)

            logger.debug(
                "[vclipInternal] Headed up: {}, start: {}, end: {}, length: {}",
                headedUp,
                startPos,
                endPos,
                length
            )

            for (i in 0..(length).toInt()) {
                val y = if (headedUp) startPos - i else startPos + i
                val pos = Vec3d(player.x, y, player.z)
                if (playerFits(pos)) {
                    logger.debug("[vclipInternal] Found air at {}", y)

                    // TODO: this shouldn't fail, but sometimes it infinitely loops and stack overflows - why?
                    return vclipInternal(from, pos)
                }
            }

            logger.debug("[vclipInternal] Couldn't resolve air :(")
            return null
        }

        if (diff > MAX_DISTANCE_PER_TICK) {
            // We can't move this fast in one tick
            logger.debug("[vclipInternal] Too far to move in one tick (MAX_DISTANCE_PER_TICK) :(")

            val headedUp = to.y > from.y
            val diffThatFits = if (headedUp) MAX_DISTANCE_PER_TICK else -MAX_DISTANCE_PER_TICK
            return vclipInternal(from, Vec3d(from.x, from.y + diffThatFits, from.z))
        }

        val packets = mutableListOf<Packet<*>>()
        val moveCount = if (player.hasVehicle()) {
            floor(diff / MAX_MOVE_PER_PACKET).toInt()
        } else {
            stepCount
        }

        for (i in 0 until moveCount) {
            if (player.hasVehicle()) {
                // Jank way to represent a VehicleMove packet we shouldn't change pos
                packets.add(buildVehicleMove(0.0, 0.0, 0.0, 0f, 0f))
            } else {
                packets.add(PlayerMoveC2SPacket.OnGroundOnly(player.isOnGround))
            }
        }

        if (player.hasVehicle()) {
            packets.add(buildVehicleMove(to.x, to.y, to.z, player.yaw, player.pitch))
        } else {
            packets.add(PlayerMoveC2SPacket.PositionAndOnGround(to.x, to.y, to.z, player.isOnGround))
        }

        logger.debug("[vclipInternal] Crafted {} packets to {}", packets.size, to.y)
        return Pair(packets, to.y)
    }

    private fun hclipInternal(from: Vec3d, to: Vec3d): Pair<List<Packet<*>>, Vec3d>? {
        val player = Tangerine.mc.player ?: return null

        val diff = to.subtract(from)
        val stepCount = ceil(diff.length() / MAX_MOVE_PER_PACKET).toInt()
        val step = diff.multiply(1.0 / stepCount)

        logger.debug("[hclipInternal] Diff: {}, step count: {}, step: {}", diff, stepCount, step)
        if (diff.length() < ARRIVAL_TOLERANCE) {
            logger.debug("[hclipInternal] Uh... already here?")
            return Pair(listOf(), to)
        }

        if (stepCount > MAX_MOVE_PER_PACKET) {
            // We can't make it all the way, and we're gonna have to cut it short - let's try and clip as far as possible
            logger.debug("[hclipInternal] Not going to make it on this tick (MAX_MOVE_PER_PACKET) :(")

            val maxReach = step.multiply(MAX_MOVE_PER_PACKET.toDouble() - 1.0)
            val newTo = from.add(maxReach)
            logger.debug("[hclipInternal] Compromising with {} (new = {})", maxReach, newTo)

            return hclipInternal(from, newTo)
        }

        if (diff.length() > MAX_DISTANCE_PER_TICK) {
            // We can't move this fast in one tick, so we're gonna have to split it up
            logger.debug("[hclipInternal] Not going to make it in one tick (MAX_DISTANCE_PER_TICK) :(")

            val diffThatFits = diff.normalize().multiply(MAX_DISTANCE_PER_TICK)
            return hclipInternal(from, from.add(diffThatFits))
        }

        val packets = mutableListOf<Packet<*>>()
        for (i in 0 until stepCount) {
            val pos = step.multiply(i.toDouble()).add(from)
            if (!playerFits(pos)) {
                // The trip we're trying to make is impossible, because we collide with something.
                // This can be solved with more efficient pathfinding (like we're doing with air pockets in vclip),
                // but it is far too much effort for my peanut brain.
                // Returning null here will resolve it into a vclip->hclip->vclip combo, which is good enough.
                logger.debug("[hclipInternal] Step {} can't fit player!", i)
                return null
            }

            if (player.hasVehicle()) {
                packets.add(buildVehicleMove(pos.x, pos.y, pos.z, player.yaw, player.pitch))
            } else {
                packets.add(PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, player.isOnGround))
            }
        }

        logger.debug("[hclipInternal] Crafted {} packets to {}", packets.size, to)
        return Pair(packets, to)
    }

    fun buildVehicleMove(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float
    ): VehicleMoveC2SPacket {
        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
        buf.writeFloat(yaw)
        buf.writeFloat(pitch)
        return VehicleMoveC2SPacket(buf)
    }
}
