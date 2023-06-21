package pm.n2.tangerine.mixin;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;

@ClientOnly
@Mixin(ClientChunkManager.ClientChunkMap.class)
public interface ClientChunkMapAccessor {
    @Accessor("chunks")
    AtomicReferenceArray<WorldChunk> getChunks();
}
