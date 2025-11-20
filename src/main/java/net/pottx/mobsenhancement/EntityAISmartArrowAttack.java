package net.pottx.mobsenhancement;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityWitherAccess;

public class EntityAISmartArrowAttack extends EntityAIBase
{
    private final EntityLivingBase entityOwner;
    private final IRangedAttackMob entityRangedAttackOwner;

    private EntityLivingBase entityAttackTarget;

    private int attackCooldownCounter;
    private final float entityMoveSpeed;

    private int canSeeTargetCounter;

    private final int minHealth;

    private final int attackInterval;
    private final double attackRangeSq;
    private final double fleeRangeSq;

    private boolean shouldFlee;
    private boolean isFleeing;

	public EntityAISmartArrowAttack(IRangedAttackMob rangedAttackMob, float moveSpeed, int attackInterval, int minHealth, float attackRange, float fleeRange) {
        canSeeTargetCounter = 0;

        entityRangedAttackOwner = rangedAttackMob;
        entityOwner = (EntityLivingBase) rangedAttackMob;
        entityMoveSpeed = moveSpeed;
        this.attackInterval = attackInterval;
        attackCooldownCounter = attackInterval >> 1;
        this.minHealth = minHealth;
        attackRangeSq = attackRange * attackRange;
        fleeRangeSq = fleeRange * fleeRange;

        setMutexBits(3);
    }

    public boolean shouldExecute() {
        if (entityOwner instanceof EntityLiving livingOwner) {
            EntityLivingBase target = livingOwner.getAttackTarget();

            if (target == null) {
                return false;
            } else if (this.entityOwner instanceof EntityWither && ((EntityWitherAccess) this.entityOwner).getIsDoingSpecialAttack()) {
                return false;
            } else {
                shouldFlee = target instanceof EntityPlayer || target.getAttackTarget() == entityOwner;
                if (shouldFlee && entityOwner.getHealth() < minHealth) {
                    return false;
                } else {
                    entityAttackTarget = target;
                    return true;
                }
            }
        }
    }

    public boolean continueExecuting() {
        return this.shouldExecute() || !entityOwner.getNavigator().noPath();
    }

    public void resetTask() {
        entityAttackTarget = null;
        canSeeTargetCounter = 0;
        attackCooldownCounter = attackInterval;
    }

    public void startExecuting() {
        canSeeTargetCounter = 20;
    }

    public void updateTask() {
        double dDistSqToTarget = entityOwner.getDistanceSq(entityAttackTarget.posX, entityAttackTarget.boundingBox.minY, entityAttackTarget.posZ);

        boolean bCanSeeTarget = entityOwner.getEntitySenses().canSee(entityAttackTarget);

        if (bCanSeeTarget) {
            ++canSeeTargetCounter;
        } else {
            canSeeTargetCounter = 0;
        }

        if (shouldFlee && dDistSqToTarget <= fleeRangeSq && canSeeTargetCounter >= 10) {
            if (attackCooldownCounter <= 5) {
                isFleeing = false;
                entityOwner.getNavigator().tryMoveToEntityLiving(entityAttackTarget, 0.25F);
            } else {
                fleeFromTarget();
            }
        } else if (dDistSqToTarget <= attackRangeSq && canSeeTargetCounter >= 20) {
            isFleeing = false;
            entityOwner.getNavigator().clearPathEntity();
        } else {
            isFleeing = false;
            entityOwner.getNavigator().tryMoveToEntityLiving(entityAttackTarget, entityMoveSpeed);
        }

        entityOwner.getLookHelper().setLookPositionWithEntity(entityAttackTarget, 30.0F, 30.0F);

        if (attackCooldownCounter > 1) {
            attackCooldownCounter--;
        } else {
            if (dDistSqToTarget <= attackRangeSq && bCanSeeTarget) {
                entityRangedAttackOwner.attackEntityWithRangedAttack(entityAttackTarget, 1F);
                attackCooldownCounter = attackInterval;
            }
        }
    }

    private void fleeFromTarget() {
        Vec3 destination = null;

        if (!isFleeing || entityOwner.getNavigator().noPath() || entityOwner.getNavigator().getPath().isFinished()) {
            isFleeing = true;
            for (int i=0; i<8; i++) {
                if (destination == null || entityAttackTarget.getDistanceSq(destination.xCoord, destination.yCoord, destination.zCoord) <= entityAttackTarget.getDistanceSqToEntity(entityOwner)) {
                    destination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
                            (EntityCreature) entityOwner,
                            16,
                            7,
                            entityOwner.worldObj.getWorldVec3Pool().getVecFromPool(entityAttackTarget.posX, entityAttackTarget.posY, entityAttackTarget.posZ)
                    );
                } else {
                    break;
                }
            }
        }

        if (destination != null) {
            entityOwner.getNavigator().setPath(
                    entityOwner.getNavigator().getPathToXYZ(destination.xCoord, destination.yCoord, destination.zCoord),
                    entityMoveSpeed
            );
        }
    }
}
