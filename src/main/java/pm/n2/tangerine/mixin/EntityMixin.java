package pm.n2.tangerine.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.tangerine.modules.movement.NoSlowModule;

@ClientOnly
@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    protected Vec3d movementMultiplier;

    @Inject(method = "move", at = @At("HEAD"))
    public void tangerine$noSlow(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        var self = (Entity) (Object) this;
        if (self instanceof PlayerEntity && NoSlowModule.INSTANCE.getEnabled().getBooleanValue()) {
            this.movementMultiplier = Vec3d.ZERO;
        }
    }
}
