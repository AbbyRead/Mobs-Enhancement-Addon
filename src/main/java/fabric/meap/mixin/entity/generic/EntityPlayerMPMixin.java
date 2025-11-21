package fabric.meap.mixin.entity.generic;

import net.minecraft.src.*;
import btw.community.abbyread.meap.extension.EntityPlayerExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin extends EntityPlayer {
    @SuppressWarnings("unused")
    private EntityPlayerMPMixin(World world, String username) {
        super(world, username);
    }

    @Inject(
            method = "isInGloom()Z",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void gloomIfStarringAtEnd(CallbackInfoReturnable<Boolean> cir) {
        if (((EntityPlayerExtend)this).meap$isCloseToEnd()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "updateGloomState()V",
            at = @At("HEAD")
    )
    private void fasterInGloomCountWhenCloseToEnd(CallbackInfo ci) {
        if (!this.isDead && ((EntityPlayerExtend)this).meap$isCloseToEnd()) {
            inGloomCounter += 3;
        }
    }
}
