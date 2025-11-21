package fabric.meap.mixin.ai;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAICreeperSwell.class)
public abstract class CreeperSwellBehaviorMixin extends EntityAIBase {
    @Shadow public EntityCreeper swellingCreeper;
    @Shadow public EntityLivingBase creeperAttackTarget;

    @Inject(
            method = "shouldExecute()Z",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void igniteFurtherAcrossWall(CallbackInfoReturnable<Boolean> cir) {
        EntityLiving target = (EntityLiving) this.swellingCreeper.getAttackTarget();

        if (target == null) {
            cir.setReturnValue(false);
        } else {
            double explodeDistanceSq = this.swellingCreeper.getEntitySenses().canSee(this.swellingCreeper.getAttackTarget()) ? 9.0D : 16.0D;
            cir.setReturnValue(this.swellingCreeper.getCreeperState() > 0 || this.swellingCreeper.getDistanceSqToEntity(target) < explodeDistanceSq);
        }
    }

    @Redirect(
            method = "updateTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityCreeper;getDistanceSqToEntity(Lnet/minecraft/src/Entity;)D")
    )
    private double calmDownFurtherAcrossWall(EntityCreeper EntityCreeper, Entity par1Entity) {
        double distance = this.swellingCreeper.getDistanceSqToEntity(creeperAttackTarget);
        return this.swellingCreeper.getEntitySenses().canSee(creeperAttackTarget) ? distance : distance - 1.0D;
    }

    @SuppressWarnings({"unused", "SameReturnValue"})
    @Redirect(
            method = "updateTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z")
    )
    private boolean dontCheckSenses(EntitySenses ignoreEntitySenses, Entity ignoreEntity) {
        return true;
    }
}
