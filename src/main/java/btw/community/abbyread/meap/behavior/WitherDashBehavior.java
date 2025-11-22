package btw.community.abbyread.meap.behavior;

import btw.block.BTWBlocks;
import net.minecraft.src.*;
import btw.community.abbyread.meap.extend.EntityWitherExtend;
import fabric.meap.mixin.access.EntityWitherAccess;

import java.util.List;

public class WitherDashBehavior extends EntityAIBase {
    private final EntityWither myWither;
    private EntityLivingBase dashTarget;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double targetDistance;
    private int dashCooldownCounter;
    private int dashProcessCounter;

    public WitherDashBehavior(EntityWither myWither) {
        this.myWither = myWither;
        this.dashCooldownCounter = 80 + this.myWither.rand.nextInt(40);
    }

    @Override
    public boolean shouldExecute() {
        if (this.myWither.isEntityAlive() && ((EntityWitherAccess)this.myWither).invokeGetSpawnInvulnerabilityTime() <= 0 && this.myWither.isArmored()) {
            this.dashCooldownCounter--;
        }

        return this.myWither.isEntityAlive() && this.myWither.getAttackTarget() != null &&
                ((EntityWitherAccess)this.myWither).invokeGetSpawnInvulnerabilityTime() <= 0 && this.myWither.isArmored() && this.dashCooldownCounter <= 0;
    }

    public boolean continueExecuting() {
        return this.dashProcessCounter > 0;
    }

    public void resetTask() {
        super.resetTask();

        this.dashCooldownCounter = (this.myWither.posY > 256D ? 40 : 80) + this.myWither.rand.nextInt(40);
        ((EntityWitherExtend)this.myWither).meap$setIsDoingSpecialAttack(false);
    }

    public void startExecuting() {
        ((EntityWitherExtend)this.myWither).meap$setIsDoingSpecialAttack(true);
        this.dashTarget = this.myWither.getAttackTarget();
        this.dashProcessCounter = 40;
    }

    public void updateTask() {
        this.dashProcessCounter --;
        this.myWither.getNavigator().clearPathEntity();

        if (this.dashProcessCounter > 15) {
            this.myWither.getLookHelper().setLookPositionWithEntity(this.dashTarget, 30.0F, 30.0F);
        } else if (this.dashProcessCounter == 15) {
            this.myWither.worldObj.playSoundEffect(this.myWither.posX, this.myWither.posY, this.myWither.posZ, "mob.wither.idle", 2F, 2.0F);

            this.targetX = this.dashTarget.posX - this.myWither.posX;
            this.targetY = this.dashTarget.posY + 0.5 * this.dashTarget.height - this.myWither.posY - 0.5 * this.myWither.height;
            this.targetZ = this.dashTarget.posZ - this.myWither.posZ;
            this.targetDistance = MathHelper.sqrt_double(targetX * targetX + targetY * targetY + targetZ * targetZ);
        } else {
            this.myWither.getLookHelper().setLookPosition(this.targetX, this.targetY, this.targetZ, 30.0F, 30.0F);
            this.myWither.setVelocity(1.25D * targetX / targetDistance, 1.25D * targetY / targetDistance, 1.25D * targetZ / targetDistance);

            if (this.myWither.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
            {
                int blockX = MathHelper.floor_double(this.myWither.posX);
                int blockY = MathHelper.floor_double(this.myWither.posY);
                int blockZ = MathHelper.floor_double(this.myWither.posZ);
                boolean canDestroy = false;

                for (int i = -1; i <= 1; ++i) {
                    for (int j = -1; j <= 1; ++j) {
                        for (int k = 0; k <= 3; ++k) {
                            int destroyX = blockX + i;
                            int destroyY = blockY + k;
                            int destroyZ = blockZ + j;
                            int blockToDestroy = this.myWither.worldObj.getBlockId(destroyX, destroyY, destroyZ);

                            if (blockToDestroy > 0 && blockToDestroy != Block.bedrock.blockID && blockToDestroy != Block.endPortal.blockID && blockToDestroy != Block.endPortalFrame.blockID &&
                                    blockToDestroy != BTWBlocks.soulforgedSteelBlock.blockID ) {
                                canDestroy = this.myWither.worldObj.destroyBlock(destroyX, destroyY, destroyZ, true) || canDestroy;
                            }
                        }
                    }
                }

                if ((this.dashProcessCounter + 6) % 10 == 0 && canDestroy) {
                    this.myWither.worldObj.playAuxSFX(1012, (int)this.myWither.posX, (int)this.myWither.posY, (int)this.myWither.posZ, 0);
                }
            }

            @SuppressWarnings("rawtypes") List nearEntities = this.myWither.worldObj.getEntitiesWithinAABB(Entity.class, this.myWither.boundingBox.expand(4.0D, 4.0D, 4.0D));

	        for (Object entity : nearEntities) {
		        Entity nearEntity = (Entity) entity;
		        double combinedWidth = this.myWither.width + nearEntity.width;
		        if (this.myWither.getDistanceSq(nearEntity.posX, nearEntity.posY, nearEntity.posZ) <= combinedWidth * combinedWidth) {
			        this.myWither.attackEntityAsMob(nearEntity);
		        }
	        }
        }
    }
}
