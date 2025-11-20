package net.pottx.mobsenhancement;

import btw.util.RandomPositionGenerator;
import net.minecraft.src.*;

public class AnimalCombatBehavior extends EntityAIBase {
    private final EntityAnimal theAnimal;
    private EntityLiving targetEntity;
    private PathEntity animalPathEntity;
    private final float animalApproachSpeed;
    private final float animalFleeSpeed;
	private boolean aggressiveMode;
    private int attackTimer;
    private int pathTimer;
	private final Class fearedEntityClass;
	private final int healthBoundary;

    public AnimalCombatBehavior(EntityAnimal theAnimal, float animalApproachSpeed, float animalFleeSpeed, @SuppressWarnings("rawtypes") Class fearedEntityClass, int healthBoundary)
    {
        this.theAnimal = theAnimal;
        this.animalApproachSpeed = animalApproachSpeed;
        this.animalFleeSpeed = animalFleeSpeed;
	    this.fearedEntityClass = fearedEntityClass;
	    this.healthBoundary = healthBoundary;
        this.pathTimer = 8;
        this.attackTimer = 5;
        this.aggressiveMode = false;
    }

    @Override
    public boolean shouldExecute() {
        if (this.theAnimal.getHealth() > this.healthBoundary && this.theAnimal.hasAttackTarget()) {
            this.aggressiveMode = true;
            this.targetEntity = (EntityLiving) this.theAnimal.getAttackTarget();
            this.animalPathEntity = this.theAnimal.getNavigator().getPathToEntity(this.targetEntity);
            return true;
        } else if (this.theAnimal.getAITarget() != null || this.theAnimal.fleeingTick > 0) {
            this.aggressiveMode = false;
            this.targetEntity = this.theAnimal.getAITarget() != null ? (EntityLiving) this.theAnimal.getAITarget() : this.theAnimal;
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
            if (this.theAnimal.getHealth() <= this.healthBoundary) {
                this.aggressiveMode = false;
            } else {
                EntityLiving target = (EntityLiving) this.theAnimal.getAttackTarget();
                return target != null && (this.targetEntity.isEntityAlive() && (this.theAnimal.getDistanceSqToEntity(target) < 32D * 32D || this.theAnimal.getEntitySenses().canSee(this.targetEntity)));
            }
        } else {
	        return this.theAnimal.getAITarget() != null || this.theAnimal.fleeingTick > 0;
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
            double reach = theAnimal.width + targetEntity.width + 0.5D;

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
