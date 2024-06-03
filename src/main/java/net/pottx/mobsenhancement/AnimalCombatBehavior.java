package net.pottx.mobsenhancement;

import btw.util.RandomPositionGenerator;
import net.minecraft.src.*;

public class AnimalCombatBehavior extends EntityAIBase {
    private EntityAnimal theAnimal;
    private EntityLiving targetEntity;
    private PathEntity animalPathEntity;
    private float distanceFromTarget;
    private float animalApproachSpeed;
    private float animalFleeSpeed;
    private Class fearedEntityClass;
    private boolean aggressiveMode;
    private int attackTimer;
    private int pathTimer;
    private int healthBoundry;

    public AnimalCombatBehavior(EntityAnimal theAnimal, float animalApproachSpeed, float animalFleeSpeed, Class fearedEntityClass, int healthBoundry)
    {
        this.theAnimal = theAnimal;
        this.animalApproachSpeed = animalApproachSpeed;
        this.animalFleeSpeed = animalFleeSpeed;
        this.fearedEntityClass = fearedEntityClass;
        this.healthBoundry = healthBoundry;
        this.pathTimer = 8;
        this.attackTimer = 5;
        this.aggressiveMode = false;
    }

    @Override
    public boolean shouldExecute() {
        if (this.theAnimal.getHealth() > this.healthBoundry && this.theAnimal.hasAttackTarget()) {
            this.aggressiveMode = true;
            this.targetEntity = this.theAnimal.getAttackTarget();
            this.animalPathEntity = this.theAnimal.getNavigator().getPathToEntity(this.targetEntity);
            return true;
        } else if (this.theAnimal.getAITarget() != null || this.theAnimal.fleeingTick > 0) {
            this.aggressiveMode = false;
            this.targetEntity = this.theAnimal.getAITarget() != null ? this.theAnimal.getAITarget() : this.theAnimal;
            Vec3 destination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theAnimal, 8, 6, this.theAnimal.worldObj.getWorldVec3Pool().getVecFromPool(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));
            if (destination == null) {
                return false;
            } else if (this.targetEntity.getDistanceSq(destination.xCoord, destination.yCoord, destination.zCoord) <= this.targetEntity.getDistanceSqToEntity(this.theAnimal)) {
                return false;
            } else {
                this.animalPathEntity = this.theAnimal.getNavigator().getPathToXYZ(destination.xCoord, destination.yCoord, destination.zCoord);
                return true;
            }
        }
        return false;
    }

    public boolean continueExecuting() {
        if (this.aggressiveMode) {
            if (this.theAnimal.getHealth() <= this.healthBoundry) {
                this.aggressiveMode = false;
            } else {
                EntityLiving target = this.theAnimal.getAttackTarget();
                return target == null ? false : (!this.targetEntity.isEntityAlive() ? false : this.theAnimal.getDistanceSqToEntity(target) < 32D * 32D || this.theAnimal.getEntitySenses().canSee(this.targetEntity));
            }
        } else if (this.theAnimal.getAITarget() != null || this.theAnimal.fleeingTick > 0) {
            return true;
        }
        return false;
    }

    public void startExecuting() {
        if (this.aggressiveMode) this.theAnimal.getNavigator().setPath(this.animalPathEntity, this.animalApproachSpeed);
        else this.theAnimal.getNavigator().setPath(this.animalPathEntity, this.animalFleeSpeed);
    }

    public void resetTask() {
        if (this.theAnimal.getAttackTarget() == this.targetEntity) {
            this.theAnimal.setAttackTarget(null);
        }
        this.targetEntity = null;
        this.theAnimal.getNavigator().clearPathEntity();
    }

    public void updateTask() {
        if (this.aggressiveMode) {
            this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);

            if (this.theAnimal.getEntitySenses().canSee(this.targetEntity) && --this.pathTimer <= 0) {
                this.pathTimer = 4 + this.theAnimal.getRNG().nextInt(7);
                this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.animalApproachSpeed);
            }

            this.attackTimer = Math.max(this.attackTimer - 1, 0);
            double reach = theAnimal.width + targetEntity.width;

            if ( targetEntity == this.theAnimal.riddenByEntity ) {
                return;
            }

            if (this.theAnimal.getEntitySenses().canSee(this.targetEntity) &&
                    this.theAnimal.getDistanceSq(this.targetEntity.posX, this.targetEntity.boundingBox.minY, this.targetEntity.posZ) <= reach) {
                if (this.attackTimer <= 0) {
                    this.attackTimer = 20;

                    this.theAnimal.attackEntityAsMob(this.targetEntity);
                }
            }
        } else if (this.theAnimal.getNavigator().noPath()) {
            Vec3 destination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theAnimal, 8, 6, this.theAnimal.worldObj.getWorldVec3Pool().getVecFromPool(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));
            if (destination != null && this.targetEntity.getDistanceSq(destination.xCoord, destination.yCoord, destination.zCoord) > this.targetEntity.getDistanceSqToEntity(this.theAnimal)) {
                this.animalPathEntity = this.theAnimal.getNavigator().getPathToXYZ(destination.xCoord, destination.yCoord, destination.zCoord);
                this.theAnimal.getNavigator().setPath(this.animalPathEntity, this.animalFleeSpeed);
            }
        }
    }
}
