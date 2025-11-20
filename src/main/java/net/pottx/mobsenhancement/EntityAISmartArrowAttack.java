package net.pottx.mobsenhancement;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.extend.EntityWitherExtend;
import net.pottx.mobsenhancement.mixin.access.EntityLivingAccess;

public class EntityAISmartArrowAttack extends EntityAIBase
{
    private final EntityLivingBase entityWielding;
    private final IRangedAttackMob rangedAttackMob;

    private EntityLivingBase entityTarget;

    private int attackCooldownCounter;
    private final float targetMoveSpeed;

    private int canSeeTargetCounter;

    private final int minHealth;

    private final int attackInterval;
    private final double attackRangeSq;
    private final double fleeRangeSq;

    private boolean shouldFlee;
    private boolean isFleeing;

	public EntityAISmartArrowAttack(IRangedAttackMob rangedAttackMob, float moveSpeed, int attackInterval, int minHealth, float attackRange, float fleeRange) {
        canSeeTargetCounter = 0;

        this.rangedAttackMob = rangedAttackMob;
        entityWielding = (EntityLivingBase) rangedAttackMob;
        targetMoveSpeed = moveSpeed;
        this.attackInterval = attackInterval;
        attackCooldownCounter = attackInterval >> 1;
        this.minHealth = minHealth;
        attackRangeSq = attackRange * attackRange;
        fleeRangeSq = fleeRange * fleeRange;

        setMutexBits(3);
    }

    public boolean shouldExecute() {
        if (entityWielding instanceof EntityLiving livingOwner) {
            EntityLivingBase target = livingOwner.getAttackTarget();

            if (target == null) {
                return false;
            } else if (this.entityWielding instanceof EntityWither wither && ((EntityWitherExtend)wither).mea$getIsDoingSpecialAttack()) {
                return false;
            } else {
                shouldFlee = target instanceof EntityPlayer || ((EntityLiving)target).getAttackTarget() == entityWielding;
                if (shouldFlee && entityWielding.getHealth() < minHealth) {
                    return false;
                } else {
                    entityTarget = target;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean continueExecuting() {
        return this.shouldExecute() || !((EntityLiving)entityWielding).getNavigator().noPath();
    }

    public void resetTask() {
        entityTarget = null;
        canSeeTargetCounter = 0;
        attackCooldownCounter = attackInterval;
    }

    public void startExecuting() {
        canSeeTargetCounter = 20;
    }

    public void updateTask() {
        double dDistSqToTarget = entityWielding.getDistanceSq(entityTarget.posX, entityTarget.boundingBox.minY, entityTarget.posZ);

        boolean bCanSeeTarget = ((EntityLivingAccess)entityWielding).getSenses().canSee(entityTarget);

        if (bCanSeeTarget) {
            ++canSeeTargetCounter;
        } else {
            canSeeTargetCounter = 0;
        }

        if (shouldFlee && dDistSqToTarget <= fleeRangeSq && canSeeTargetCounter >= 10) {
            if (attackCooldownCounter <= 5) {
                isFleeing = false;
                ((EntityLivingAccess)entityWielding).getNavigator().tryMoveToEntityLiving(entityTarget, 0.25F);
            } else {
                fleeFromTarget();
            }
        } else if (dDistSqToTarget <= attackRangeSq && canSeeTargetCounter >= 20) {
            isFleeing = false;
            ((EntityLivingAccess)entityWielding).getNavigator().clearPathEntity();
        } else {
            isFleeing = false;
            ((EntityLivingAccess)entityWielding).getNavigator().tryMoveToEntityLiving(entityTarget, targetMoveSpeed);
        }

        ((EntityLivingAccess)entityWielding).getLookHelper().setLookPositionWithEntity(entityTarget, 30.0F, 30.0F);

        if (attackCooldownCounter > 1) {
            attackCooldownCounter--;
        } else {
            if (dDistSqToTarget <= attackRangeSq && bCanSeeTarget) {
                rangedAttackMob.attackEntityWithRangedAttack(entityTarget, 1F);
                attackCooldownCounter = attackInterval;
            }
        }
    }

    private void fleeFromTarget() {
        Vec3 destination = null;

        if (!isFleeing || ((EntityLivingAccess)entityWielding).getNavigator().noPath() || ((EntityLivingAccess)entityWielding).getNavigator().getPath().isFinished()) {
            isFleeing = true;
            for (int i=0; i<8; i++) {
                if (destination == null || entityTarget.getDistanceSq(destination.xCoord, destination.yCoord, destination.zCoord) <= entityTarget.getDistanceSqToEntity(entityWielding)) {
                    destination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
                            (EntityCreature) entityWielding,
                            16,
                            7,
                            entityWielding.worldObj.getWorldVec3Pool().getVecFromPool(entityTarget.posX, entityTarget.posY, entityTarget.posZ)
                    );
                } else {
                    break;
                }
            }
        }

        if (destination != null) {
            ((EntityLivingAccess)entityWielding).getNavigator().setPath(
                    ((EntityLivingAccess)entityWielding).getNavigator().getPathToXYZ(destination.xCoord, destination.yCoord, destination.zCoord),
                    targetMoveSpeed
            );
        }
    }
}
