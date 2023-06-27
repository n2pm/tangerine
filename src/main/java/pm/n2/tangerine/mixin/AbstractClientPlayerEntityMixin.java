package pm.n2.tangerine.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import pm.n2.tangerine.modules.movement.NoSlowModule;

@ClientOnly
@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @ModifyArg(method = "getSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"), index = 2)
    public float tangerine$noSlow(float delta) {
        if (delta < 1.0F && NoSlowModule.INSTANCE.getEnabled().getValue()) {
            delta = 1.0F;
        }

        return delta;
    }
}
