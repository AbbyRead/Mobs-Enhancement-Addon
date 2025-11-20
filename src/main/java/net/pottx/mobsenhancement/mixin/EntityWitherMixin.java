package net.pottx.mobsenhancement.mixin;

import btw.block.BTWBlocks;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAISmartArrowAttack;
import net.pottx.mobsenhancement.WitherDashBehavior;
import net.pottx.mobsenhancement.WitherSummonMinionBehavior;
import net.pottx.mobsenhancement.extend.EntityWitherExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob implements IRangedAttackMob, EntityWitherExtend, EntityWitherAccess {
    @Unique
    EntityWither self = (EntityWither) (Object) this;

    @Unique
    public boolean isDoingSpecialAttack;

    @Shadow
    public abstract int getWatchedTargetId(int par1);

    public EntityWitherMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addSpecialAttackTasks(CallbackInfo ci) {
        this.tasks.addTask(1, new WitherSummonMinionBehavior(self)); 
        this.tasks.addTask(1, new WitherDashBehavior(self)); 
    }

    @Override
    public int getMeleeAttackStrength(Entity target) {
        return 10;
    }

    @Override
    protected void updateAITasks()
    {
        int var1;

        if (this.invokeGetSpawnInvulnerabilityTime() > 0)
        {
            var1 = this.invokeGetSpawnInvulnerabilityTime() - 1;

            if (var1 <= 0)
            {
                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
                this.worldObj.func_82739_e(1013, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            }

            this.invokeSetSpawnInvulnerabilityTime(var1);

            if (this.ticksExisted % 10 == 0)
            {
                this.heal(10);
            }
        }
        else
        {
            super.updateAITasks();
            int var12;

            for (var1 = 1; var1 < 3; ++var1)
            {
                if (!this.mea$getIsDoingSpecialAttack() && this.ticksExisted >= this.getNextHeadAttackTime()[var1 - 1])
                {
                    this.getNextHeadAttackTime()[var1 - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);

                    {
                        int var10001 = var1 - 1;
                        int var10003 = this.getIdleHeadTicks()[var1 - 1];
                        this.getIdleHeadTicks()[var10001] = this.getIdleHeadTicks()[var1 - 1] + 1;

                        if (var10003 > 15)
                        {
                            float var2 = 10.0F;
                            float var3 = 5.0F;
                            double var4 = MathHelper.getRandomDoubleInRange(this.rand, this.posX - (double)var2, this.posX + (double)var2);
                            double var6 = MathHelper.getRandomDoubleInRange(this.rand, this.posY - (double)var3, this.posY + (double)var3);
                            double var8 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - (double)var2, this.posZ + (double)var2);
                            this.invokeShootSkullAt(var1 + 1, var4, var6, var8, true);
                            this.getIdleHeadTicks()[var1 - 1] = 0;
                        }
                    }

                    var12 = this.getWatchedTargetId(var1);

                    if (var12 > 0)
                    {
                        Entity var14 = this.worldObj.getEntityByID(var12);

                        if (var14 != null && var14.isEntityAlive() && this.getDistanceSqToEntity(var14) <= 900.0D && this.canEntityBeSeen(var14))
                        {
 
                            this.invokeFireSkullAtEntity(var1 + 1, (EntityLivingBase)var14);
                            this.getNextHeadAttackTime()[var1 - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                            this.getIdleHeadTicks()[var1 - 1] = 0;
                        }
                        else
                        {
                            this.invokeSetHeadTarget(var1, 0);
                        }
                    }
                    else
                    {
                        @SuppressWarnings("rawtypes") List var13 = this.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(20.0D, 8.0D, 20.0D), EntityWitherAccess.getValidTargetSelector());

                        for (int var16 = 0; var16 < 10 && !var13.isEmpty(); ++var16)
                        {
                            EntityLiving var5 = (EntityLiving)var13.get(this.rand.nextInt(var13.size()));

                            if (var5 != this && var5.isEntityAlive() && this.canEntityBeSeen(var5))
                            {
                                if (var5 instanceof EntityPlayer)
                                {
                                    if (!((EntityPlayer)var5).capabilities.disableDamage)
                                    {
                                        this.invokeSetHeadTarget(var1, var5.entityId);
                                    }
                                }
                                else
                                {
                                    this.invokeSetHeadTarget(var1, var5.entityId);
                                }

                                break;
                            }

                            var13.remove(var5);
                        }
                    }
                }
            }

            if (this.invokeGetAttackTarget() != null)
            {
                this.invokeSetHeadTarget(0, this.invokeGetAttackTarget().entityId);
            }
            else
            {
                this.invokeSetHeadTarget(0, 0);
            }

            if (!this.mea$getIsDoingSpecialAttack() && this.getBlockBreakCounter() > 0)
            {
                this.setBlockBreakCounter(this.getBlockBreakCounter() - 1);

                if (this.getBlockBreakCounter() == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                {
                    var1 = MathHelper.floor_double(this.posY);
                    var12 = MathHelper.floor_double(this.posX);
                    int var15 = MathHelper.floor_double(this.posZ);
                    boolean var18 = false;

                    for (int var17 = -1; var17 <= 1; ++var17)
                    {
                        for (int var19 = -1; var19 <= 1; ++var19)
                        {
                            for (int var7 = 0; var7 <= 3; ++var7)
                            {
                                int var20 = var12 + var17;
                                int var9 = var1 + var7;
                                int var10 = var15 + var19;
                                int var11 = this.worldObj.getBlockId(var20, var9, var10);

                                if (var11 > 0 && var11 != Block.bedrock.blockID && var11 != Block.endPortal.blockID && var11 != Block.endPortalFrame.blockID &&
                                        var11 != BTWBlocks.soulforgedSteelBlock.blockID )
                                {
                                    var18 = this.worldObj.destroyBlock(var20, var9, var10, true) || var18;
                                }
                            }
                        }
                    }

                    if (var18)
                    {
                        this.worldObj.playAuxSFXAtEntity(null, 1012, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    }
                }
            }

            if (this.ticksExisted % 20 == 0)
            {
                this.heal(1);
            }
        }
    }

    @Override
    public boolean meleeAttack(Entity target)
    {
        setLastAttackingEntity( target );

        int iStrength = getMeleeAttackStrength(target);

        if ( isPotionActive( Potion.damageBoost ) )
        {
            iStrength += 3 << getActivePotionEffect( Potion.damageBoost ).getAmplifier();
        }

        if ( isPotionActive( Potion.weakness ) )
        {
            iStrength -= 2 << getActivePotionEffect( Potion.weakness ).getAmplifier();
        }

        int iKnockback = 2;

        boolean bAttackSuccess = target.attackEntityFrom( DamageSource.causeMobDamage( this ),
                iStrength );

        if ( bAttackSuccess )
        {
            target.addVelocity(
                    -MathHelper.sin( rotationYaw * (float)Math.PI / 180F ) * iKnockback * 0.5F,
                    0.1D,
                    MathHelper.cos( rotationYaw * (float)Math.PI / 180F ) * iKnockback * 0.5F );

            motionX *= 0.6D;
            motionZ *= 0.6D;

            int iFireModifier = EnchantmentHelper.getFireAspectModifier( this );

            if ( iFireModifier > 0 )
            {
                target.setFire( iFireModifier * 4 );
            }
            else if ( isBurning() && rand.nextFloat() < 0.6F )
            {
                target.setFire( 4 );
            }
        }

        return bAttackSuccess;
    }
 

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void modifyArrowAttackTask(Args args) {
        args.set(1, new EntityAISmartArrowAttack(this, getAIMoveSpeed(), 40, 0, 30.0F, 0.0F));
    }

    @Redirect(
            method = "onLivingUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getEntityByID(I)Lnet/minecraft/src/Entity;", ordinal = 0)
    )
    private Entity stayStillWhenDoingSpecialAttack(World world, int var1) {
        if (this.mea$getIsDoingSpecialAttack()) {
            return null;
        } else {
            return this.worldObj.getEntityByID(this.getWatchedTargetId(0));
        }
    }

    @Redirect(
            method = "onLivingUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MathHelper;sqrt_double(D)F", ordinal = 0)
    )
    private float keepFartherDistance(double var6) {
        if (var6 > 64D && !(this.mea$getIsDoingSpecialAttack())) {
            return 1.5F * MathHelper.sqrt_double(var6);
        } else {
            return Float.MAX_VALUE;
        }
    }

    @Unique
    public boolean mea$getIsDoingSpecialAttack() {
        return this.isDoingSpecialAttack;
    }

    public void mea$setIsDoingSpecialAttack(boolean isDoingSpecialAttack) {
        this.isDoingSpecialAttack = isDoingSpecialAttack;
    }
}