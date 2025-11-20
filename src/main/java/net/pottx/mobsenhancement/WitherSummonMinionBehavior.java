package net.pottx.mobsenhancement;

import net.minecraft.src.*;
import btw.item.BTWItems;
import net.pottx.mobsenhancement.extend.EntityWitherExtend;
import net.pottx.mobsenhancement.mixin.EntityWitherAccess;

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
        if (!myWither.isEntityAlive() || ((EntityWitherAccess)myWither).invokeGetSpawnInvulnerabilityTime() > 0 || myWither.isArmored()) {
            return false;
        }

        summonCooldownCounter--;
        return myWither.getAttackTarget() != null && summonCooldownCounter <= 0;
    }

    @Override
    public boolean continueExecuting() {
        return summonProcessCounter > 0;
    }

    @Override
    public void resetTask() {
        summonCooldownCounter = 160 + myWither.rand.nextInt(80);
        summonProcessCounter = 0;
        ((EntityWitherExtend) myWither).mea$setIsDoingSpecialAttack(false);
    }

    @Override
    public void startExecuting() {
        summonProcessCounter = 40; // Animation duration
        ((EntityWitherExtend) myWither).mea$setIsDoingSpecialAttack(true);

        int baseY = MathHelper.floor_double(myWither.posY);

        for (int i = 0; i < MAX_SUMMONS; i++) {
            boolean unique;
            do {
                unique = true;
                summonX[i] = MathHelper.floor_double(myWither.posX) + myWither.rand.nextInt(9) - 4;
                summonZ[i] = MathHelper.floor_double(myWither.posZ) + myWither.rand.nextInt(9) - 4;
                // Check duplicates against previous summons
                for (int j = 0; j < i; j++) {
                    if (summonX[i] == summonX[j] && summonZ[i] == summonZ[j]) {
                        unique = false;
                        break;
                    }
                }
            } while (!unique);

            summonY[i] = findValidGroundY(summonX[i], baseY, summonZ[i]);
        }

        // If no valid spawn locations, cancel summoning
        boolean hasValidY = false;
        for (int y : summonY) {
            if (y > 0) {
                hasValidY = true;
                break;
            }
        }
        if (!hasValidY) {
            summonProcessCounter = -1;
            ((EntityWitherExtend) myWither).mea$setIsDoingSpecialAttack(false);
            return;
        }

        // Play summoning sound
        myWither.worldObj.playSoundEffect(myWither.posX, myWither.posY, myWither.posZ, "mob.wither.idle", 2F, 0.25F);
    }

    @Override
    public void updateTask() {
        if (summonProcessCounter <= 0) return;

        summonProcessCounter--;
        myWither.getNavigator().clearPathEntity(); // Clear pathing while summoning

        // Optional visual effect loop while summoning
        if (summonProcessCounter > 0) {
            for (int i = 0; i < MAX_SUMMONS; i++) {
                if (summonY[i] > 0) {
                    for (int j = 0; j < 8; j++) {
                        myWither.worldObj.playAuxSFX(MEAEffectManager.WITHER_SUMMON_EFFECT_ID, summonX[i], summonY[i], summonZ[i], j);
                    }
                }
            }
        }

        // Spawn skeletons at end of summoning
        if (summonProcessCounter == 0) {
            for (int i = 0; i < MAX_SUMMONS; i++) {
                if (summonY[i] <= 0) continue;

                EntitySkeleton sk = new EntitySkeleton(myWither.worldObj);
                sk.setSkeletonType(1);
                sk.setLocationAndAngles(summonX[i] + 0.5D, summonY[i], summonZ[i] + 0.5D, myWither.rand.nextFloat() * 360F, 0);

                if (myWither.rand.nextInt(3) == 0) {
                    sk.setCurrentItemOrArmor(0, new ItemStack(BTWItems.steelSword));
                }

                sk.spawnerInitCreature();
                myWither.worldObj.spawnEntityInWorld(sk);

                EntityLivingBase target = myWither.getAttackTarget();
                if (target != null) sk.setAttackTarget(target);

                myWither.worldObj.playAuxSFX(2004, summonX[i], summonY[i], summonZ[i], 0);
            }
        }
    }

    /**
     * Find the first valid Y for a skeleton to spawn at a given X,Z near the Wither.
     */
    private int findValidGroundY(int x, int baseY, int z) {
        for (int offset = 0; offset <= 8; offset++) {
            int y = baseY - offset;
            if (canSpawnAt(x, y, z)) return y;
        }
        for (int offset = 1; offset <= 6; offset++) {
            int y = baseY + offset;
            if (canSpawnAt(x, y, z)) return y;
        }
        return -1;
    }

    private boolean canSpawnAt(int x, int y, int z) {
        EntitySkeleton temp = new EntitySkeleton(myWither.worldObj);
        temp.setSkeletonType(1);
        temp.setPosition(x + 0.5D, y, z + 0.5D);

        return myWither.worldObj.checkNoEntityCollision(temp.boundingBox) &&
                myWither.worldObj.getCollidingBoundingBoxes(temp, temp.boundingBox).isEmpty() &&
                myWither.worldObj.doesBlockHaveSolidTopSurface(x, y - 1, z);
    }
}
