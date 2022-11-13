package pm.n2.tangerine.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ClientChunkManager.class)
public interface ClientChunkManagerAccessor {
	@Accessor("chunks")
	ClientChunkManager.ClientChunkMap getChunks();
}
