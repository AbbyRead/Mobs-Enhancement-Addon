package btw.community.abbyread.meap.behavior;

import btw.community.abbyread.meap.util.MEAEffectManager;
import net.minecraft.src.*;
import btw.item.BTWItems;
import btw.community.abbyread.meap.extend.EntityWitherExtend;
import fabric.meap.mixin.access.EntityWitherAccess;

public class WitherSummonMinionBehavior extends EntityAIBase {

	private final EntityWither myWither;
	private final int MAX_SUMMONS = 3;
	private final int[] summonX = new int[MAX_SUMMONS];
	private final int[] summonY = new int[MAX_SUMMONS];
	private final int[] summonZ = new int[MAX_SUMMONS];
	private int summonCooldownCounter;
	private int summonProcessCounter;

	public WitherSummonMinionBehavior(EntityWither myWither) {
		this.myWither = myWither;
		this.summonCooldownCounter = 160 + this.myWither.rand.nextInt(80);
	}

	@Override
	public boolean shouldExecute() {
		if (!this.myWither.isEntityAlive() || ((EntityWitherAccess)this.myWither).meap$getSpawnInvulnerabilityTime() > 0 || this.myWither.isArmored()) {
			return false;
		}
		this.summonCooldownCounter--;
		return this.myWither.isEntityAlive() && this.myWither.getAttackTarget() != null &&
				((EntityWitherAccess)this.myWither).meap$getSpawnInvulnerabilityTime() <= 0 && !this.myWither.isArmored() && this.summonCooldownCounter <= 0;
	}

	public boolean continueExecuting() {
		return this.summonProcessCounter > 0;
	}

	public void resetTask() {
		super.resetTask();

		this.summonCooldownCounter = 160 + this.myWither.rand.nextInt(80);
		((EntityWitherExtend)this.myWither).meap$setIsDoingSpecialAttack(false);
	}

	public void startExecuting() {
		this.summonProcessCounter = 40;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				if (i == 0) {
					summonX[i] = MathHelper.floor_double(this.myWither.posX) + this.myWither.rand.nextInt(9) - 4;
					summonZ[i] = MathHelper.floor_double(this.myWither.posZ) + this.myWither.rand.nextInt(9) - 4;
				} else {
					boolean duplicated = true;
					while (duplicated) {
						for (int k = 0; k < i; k++) {
							summonX[i] = MathHelper.floor_double(this.myWither.posX) + this.myWither.rand.nextInt(9) - 4;
							summonZ[i] = MathHelper.floor_double(this.myWither.posZ) + this.myWither.rand.nextInt(9) - 4;
							if (summonX[i] != summonX[k] || summonZ[i] != summonZ[k]) {
								duplicated = false;
							}
						}
					}
				}
				int startingY = MathHelper.floor_double(this.myWither.posY);
				summonY[i] = startingY;
				while (summonY[i] > 0 && MathHelper.floor_double(this.myWither.posY) - summonY[i] < 64) {
					EntitySkeleton summonCheck = (EntitySkeleton) EntityList.createEntityByName("WitherSkeleton", this.myWither.worldObj);
					summonCheck.setSkeletonType(1);
					summonCheck.setPosition(summonX[i] + 0.5D, summonY[i], summonZ[i] + 0.5D);
					if (this.myWither.worldObj.checkNoEntityCollision(summonCheck.boundingBox) &&
							this.myWither.worldObj.getCollidingBoundingBoxes(summonCheck, summonCheck.boundingBox).isEmpty() &&
							this.myWither.worldObj.doesBlockHaveSolidTopSurface(summonX[i], summonY[i] - 1, summonZ[i]))
						break;
					if (summonY[i] == startingY - 8) {
						summonY[i] = startingY + 6;
					} else if (summonY[i] == startingY + 1) {
						summonY[i] = startingY - 9;
					} else {
						summonY[i] -= 1;
					}
				}
				if (summonY[i] > 0) break;
			}
		}
		if (summonY[0] > 0 || summonY[1] > 0 || summonY[2] > 0) {
			this.myWither.worldObj.playSoundEffect(this.myWither.posX, this.myWither.posY, this.myWither.posZ, "mob.wither.idle", 2F, 0.25F);

			((EntityWitherExtend) this.myWither).meap$setIsDoingSpecialAttack(true);
		}
	}

	public void updateTask() {
		if (summonY[0] > 0 || summonY[1] > 0 || summonY[2] > 0) {
			this.summonProcessCounter--;
			this.myWither.getNavigator().clearPathEntity();
			if (this.summonProcessCounter > 0) {
				for (int i = 0; i < 3; i++) {
					if (summonY[i] > 0) {
						for (int j = 0; j < 8; j++) {
							this.myWither.worldObj.playAuxSFX(MEAEffectManager.WITHER_SUMMON_EFFECT_ID, summonX[i], summonY[i], summonZ[i], j);
						}
					}
				}
			} else if (this.summonProcessCounter == 0) {
				for (int i = 0; i < 3; i++) {
					if (summonY[i] > 0) {
						EntitySkeleton summonedSkeleton = (EntitySkeleton) EntityList.createEntityByName("WitherSkeleton", this.myWither.worldObj);
						summonedSkeleton.setSkeletonType(1); // Ensure it’s the Wither Skeleton variant
						summonedSkeleton.setLocationAndAngles(summonX[i] + 0.5D, summonY[i], summonZ[i] + 0.5D, this.myWither.rand.nextFloat() * 360F, 0.0F);
						if (this.myWither.rand.nextInt(3) == 0) {
							summonedSkeleton.setCurrentItemOrArmor(0, new ItemStack(BTWItems.steelSword));
						}
						summonedSkeleton.spawnerInitCreature();
						this.myWither.worldObj.playAuxSFX(2004, this.summonX[i], this.summonY[i], this.summonZ[i], 0);
						this.myWither.worldObj.spawnEntityInWorld(summonedSkeleton);
						if (this.myWither.getAttackTarget() != null) summonedSkeleton.setAttackTarget(this.myWither.getAttackTarget());
					}
				}
			}
		} else {
			this.summonProcessCounter = -1;
		}
	}
}
