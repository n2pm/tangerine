package pm.n2.tangerine.mixin;

import net.minecraft.client.world.ClientChunkManager;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ClientOnly
@Mixin(ClientChunkManager.class)
public interface ClientChunkManagerAccessor {
    @Accessor("chunks")
    ClientChunkManager.ClientChunkMap getChunks();
}
