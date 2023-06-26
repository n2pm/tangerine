package pm.n2.tangerine.mixin;

import net.minecraft.entity.vehicle.BoatEntity;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ClientOnly
@Mixin(BoatEntity.class)
public interface BoatEntityAccessor {
    @Accessor
    BoatEntity.Location getLocation();
}
