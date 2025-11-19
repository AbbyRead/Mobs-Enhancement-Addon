package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.behavior.SimpleWanderBehavior;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntityZombie;
import net.pottx.mobsenhancement.access.EntitySkeletonAccess;
import net.pottx.mobsenhancement.access.EntityZombieAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleWanderBehavior.class)
public abstract class SimpleWanderBehaviorMixin extends EntityAIBase {
    @Shadow private EntityCreature myEntity;

    @Inject(
            method = "shouldExecute()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void shouldNotExecuteIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.myEntity instanceof EntityZombie && ((EntityZombieAccess) this.myEntity).getIsBreakingBlock()) ||
                (this.myEntity instanceof EntitySkeleton && ((EntitySkeletonAccess) this.myEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueExecuting()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void notContinueIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.myEntity instanceof EntityZombie && ((EntityZombieAccess) this.myEntity).getIsBreakingBlock()) ||
                (this.myEntity instanceof EntitySkeleton && ((EntitySkeletonAccess) this.myEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }
}
