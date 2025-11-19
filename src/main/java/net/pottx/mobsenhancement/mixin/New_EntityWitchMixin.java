package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAIFleeFromExplosion;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWitch.class)
public abstract class New_EntityWitchMixin extends EntityMob {
    public New_EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addFleeFromExplosionTask(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);

        tasks.addTask(1, new EntityAIFleeFromExplosion(this, 0.375F, 4.0F));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, VillagerEntity.class, 24.0F, 0, ((EntityMobAccess)this).getCanXray() == (byte)0));
    }

    @Override
    public int getMaxHealth() {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        return i > 0 ? 28 : 24;
    }
}
