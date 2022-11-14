package pm.n2.tangerine.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(BoatEntity.class)
public interface BoatEntityAccessor {
	@Accessor
	BoatEntity.Location getLocation();
}
