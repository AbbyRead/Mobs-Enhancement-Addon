package fabric.meap.mixin.ai;

import net.minecraft.src.*;
import btw.community.abbyread.meap.extend.EntitySkeletonExtend;
import btw.community.abbyread.meap.extend.EntityZombieExtend;
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
        if ((this.idleEntity instanceof EntityZombie && ((EntityZombieExtend) this.idleEntity).meap$getIsBreakingBlock()) ||
                (this.idleEntity instanceof EntitySkeleton && ((EntitySkeletonExtend) this.idleEntity).meap$getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueExecuting()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void notContinueIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.idleEntity instanceof EntityZombie && ((EntityZombieExtend) this.idleEntity).meap$getIsBreakingBlock()) ||
                (this.idleEntity instanceof EntitySkeleton && ((EntitySkeletonExtend) this.idleEntity).meap$getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }
}
