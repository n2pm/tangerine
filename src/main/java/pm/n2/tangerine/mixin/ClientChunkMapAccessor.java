package pm.n2.tangerine.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Environment(EnvType.CLIENT)
@Mixin(ClientChunkManager.ClientChunkMap.class)
public interface ClientChunkMapAccessor {
	@Accessor("chunks")
	AtomicReferenceArray<WorldChunk> getChunks();
}
