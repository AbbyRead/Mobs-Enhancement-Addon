package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.extend.EntityPlayerExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin extends EntityPlayer {
    public EntityPlayerMPMixin(World world, String username) {
        super(world, username);
    }

    @Inject(
            method = "isInGloom()Z",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void gloomIfStarringAtEnd(CallbackInfoReturnable<Boolean> cir) {
        if (((EntityPlayerExtend)this).isCloseToEnd()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "updateGloomState()V",
            at = @At("HEAD")
    )
    private void fasterInGloomCountWhenCloseToEnd(CallbackInfo ci) {
        if (!this.isDead && ((EntityPlayerExtend)this).isCloseToEnd()) {
            inGloomCounter += 3;
        }
    }
}
