package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntitySkeletonAccess;
import net.pottx.mobsenhancement.access.EntityZombieAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAILookIdle.class)
public abstract class EntityAILookIdleMixin extends EntityAIBase {
    @Shadow
    private EntityLivingBase idleEntity;

    @Inject(
            method = "shouldExecute()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void shouldNotExecuteIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.idleEntity instanceof EntityZombie && ((EntityZombieAccess) this.idleEntity).getIsBreakingBlock()) ||
                (this.idleEntity instanceof EntitySkeleton && ((EntitySkeletonAccess) this.idleEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueExecuting()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void notContinueIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.idleEntity instanceof EntityZombie && ((EntityZombieAccess) this.idleEntity).getIsBreakingBlock()) ||
                (this.idleEntity instanceof EntitySkeleton && ((EntitySkeletonAccess) this.idleEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }
}
