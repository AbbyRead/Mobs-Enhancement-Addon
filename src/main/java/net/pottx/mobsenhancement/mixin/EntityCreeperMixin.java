package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.extend.EntityMobExtend;
import net.pottx.mobsenhancement.mixin.access.EntityCreeperAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityCreeper.class)
public abstract class EntityCreeperMixin extends EntityMob {
    private EntityCreeperMixin(World par1World) {
        super(par1World);
    }

    @Shadow
    public abstract int getNeuteredState();

    @Shadow
    public abstract void setCreeperState(int par1);

    @Shadow
    public abstract boolean getPowered();

    @Inject(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "TAIL")
    )
    private void modifyTasksAndSetupMob(World world, CallbackInfo ci) {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);

        // Set fuse time based on game progress
        ((EntityCreeperAccess)this).setFuseTime(i > 0 ? 20 : 25);

        // Reduce xray chance
        if (((EntityMobExtend)this).mea$getCanXray() == (byte)1 && this.rand.nextInt(2) == 0) {
            ((EntityMobExtend)this).mea$setCanXray((byte)0);
        }

        // Random powered creeper chance
        if (i > 0 && this.rand.nextInt(8) == 0) {
            this.dataWatcher.updateObject(17, (byte) 1);
        }

        // Remove watch closest task
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);
    }

    @ModifyArgs(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 7)
    )
    private void modifyNearestAttackablePlayerTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 32.0F, 0, ((EntityMobExtend)this).mea$getCanXray() == (byte)0));
    }

    @ModifyArgs(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;", ordinal = 0)
    )
    private void resetExplosionCenter(Args args) {
        args.set(2, this.posY + 0.5 * this.height);
    }

    @ModifyArgs(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;", ordinal = 1)
    )
    private void resetExplosionCenterPowered(Args args) {
        args.set(2, this.posY + 0.5 * this.height);
    }

    @Inject(
            method = "interact(Lnet/minecraft/src/EntityPlayer;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityCreeper;setNeuteredState(I)V"),
            cancellable = true
    )
    private void explodeWithChance(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        int i = this.getPowered() ? 2 : 8;
        if (this.rand.nextInt(i) == 0) {
            boolean mobGriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

            if (this.getPowered()) {
                this.worldObj.createExplosion(this, this.posX, this.posY + 0.5 * this.height, this.posZ, 6F, mobGriefing);
            } else {
                this.worldObj.createExplosion(this, this.posX, this.posY + 0.5 * this.height, this.posZ, 3F, mobGriefing);
            }

            this.setDead();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    public void replaceAttackEntityFrom(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.isExplosion() && this.getNeuteredState() == 0) {
            ((EntityCreeperAccess)this).setIsDeterminedToExplode(true);
            this.setCreeperState(1);
            cir.setReturnValue(false);
            return;
        }
        cir.setReturnValue(super.attackEntityFrom(damageSource, amount));
    }



    @Inject(method = "applyEntityAttributes", at = @At("RETURN"))
    private void modifyMaxHealth(CallbackInfo ci) {
        double baseHp = 16.0;

        if (this.worldObj != null) {
            int tier = MEAUtils.getGameProgressMobsLevel(this.worldObj);
            if (tier > 0) {
                baseHp = 20.0;
            }
        }

        // Set new max HP
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setAttribute(baseHp);

        // Heal creeper to its new max HP since the super.applyEntityAttributes
        //   call may have set its health lower, and applyEntityAttributes is
        //   only used at construction time of the entity.
        this.setHealth((float) baseHp);
    }
}