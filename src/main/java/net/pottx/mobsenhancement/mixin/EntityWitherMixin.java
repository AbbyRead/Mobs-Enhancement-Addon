package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.WitherDashBehavior;
import net.pottx.mobsenhancement.WitherSummonMinionBehavior;
import net.pottx.mobsenhancement.extend.EntityWitherExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob implements EntityWitherExtend {

    @Unique
    private EntityWither self = (EntityWither)(Object)this;

    @Unique
    private boolean isDoingSpecialAttack;

    @Shadow
    public abstract int getWatchedTargetId(int head);

    public EntityWitherMixin(World world) {
        super(world);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addSpecialAttackTasks(CallbackInfo ci) {
        this.tasks.addTask(1, new WitherSummonMinionBehavior(self));
        this.tasks.addTask(1, new WitherDashBehavior(self));
    }

    // --------------------------------------------------------------------
    // The large updateAITasks override has been removed.
    // Melee override removed.
    // Redirects + ModifyArgs removed.
    // --------------------------------------------------------------------

    @Unique
    public boolean mea$getIsDoingSpecialAttack() {
        return this.isDoingSpecialAttack;
    }

    @Unique
    public void mea$setIsDoingSpecialAttack(boolean value) {
        this.isDoingSpecialAttack = value;
    }
}
