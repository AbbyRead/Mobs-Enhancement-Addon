package net.pottx.mobsenhancement;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.extend.EntityLivingExtend;
import net.pottx.mobsenhancement.extend.EntityZombieExtend;
import net.pottx.mobsenhancement.mixin.access.EntityLivingAccess;

/**
 * Enhanced attack AI that adds health-based fleeing behavior and respects entity home distances.
 * Mobs will avoid combat when their health is too low and they're being actively targeted.
 */
public class EntityAISmartAttackOnCollide extends EntityAIBase {

    private static final float LOOK_RANGE = 30.0F;
    private static final int BASE_PATHFIND_DELAY = 4;
    private static final int PATHFIND_DELAY_VARIANCE = 7;
    private static final int ATTACK_COOLDOWN_TICKS = 20;
    private static final double TOOL_REACH_BONUS = 2.0D;

    @SuppressWarnings("FieldCanBeLocal") private final World world;
    private final EntityLiving attacker;
    private final float moveSpeed;
    private final boolean hasLongMemory;
    private final int minHealthThreshold;

    private EntityLiving currentTarget;
    private PathEntity pathToTarget;
    private int pathfindCooldown;
    private int attackCooldown;

    public EntityAISmartAttackOnCollide(EntityLiving attacker, float moveSpeed, boolean hasLongMemory, int minHealthThreshold) {
        this.attacker = attacker;
        this.world = attacker.worldObj;
        this.moveSpeed = moveSpeed;
        this.hasLongMemory = hasLongMemory;
        this.minHealthThreshold = minHealthThreshold;
        this.attackCooldown = 0;
        this.setMutexBits(3); // Movement and looking
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase potentialTarget = attacker.getAttackTarget();

        if (potentialTarget == null) {
            return false;
        }

        if (isAttackerBusy()) {
            return false;
        }

        if (shouldFleeFromCombat(potentialTarget)) {
            return false;
        }

        currentTarget = (EntityLiving) potentialTarget;
        pathToTarget = attacker.getNavigator().getPathToEntityLiving(currentTarget);

        return pathToTarget != null;
    }

    @Override
    public boolean continueExecuting() {
        if (attacker.getHealth() < minHealthThreshold) {
            return false;
        }

        if (isAttackerBusy()) {
            return false;
        }

        EntityLivingBase target = ((EntityLivingAccess) attacker).getAttackTarget();

        if (target == null || !currentTarget.isEntityAlive()) {
            return false;
        }

        return isTargetWithinHomeRange();
    }

    @Override
    public void startExecuting() {
        attacker.getNavigator().setPath(pathToTarget, moveSpeed);
        pathfindCooldown = 0;
    }

    @Override
    public void resetTask() {
        // Clear target reference if we were attacking this specific entity
        if (attacker.getAttackTarget() == currentTarget) {
            attacker.setAttackTarget(null);
        }

        currentTarget = null;
        attacker.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        lookAtTarget();
        updatePathfinding();
        updateAttackCooldown();
        attemptAttack();
    }

    private boolean isAttackerBusy() {
        return attacker instanceof EntityZombie && ((EntityZombieExtend) attacker).mea$getIsBreakingBlock();
    }

    private boolean shouldFleeFromCombat(EntityLivingBase potentialTarget) {
        boolean isBeingHunted = potentialTarget instanceof EntityPlayer
                || ((EntityLiving) potentialTarget).getAttackTarget() == attacker;

        return isBeingHunted && attacker.getHealth() < minHealthThreshold;
    }

    private boolean isTargetWithinHomeRange() {
        return ((EntityLivingExtend) attacker).mea$isWithinMaximumHomeDistance(
                MathHelper.floor_double(currentTarget.posX),
                MathHelper.floor_double(currentTarget.posY),
                MathHelper.floor_double(currentTarget.posZ)
        );
    }

    private void lookAtTarget() {
        attacker.getLookHelper().setLookPositionWithEntity(currentTarget, LOOK_RANGE, LOOK_RANGE);
    }

    private void updatePathfinding() {
        boolean canSeeTarget = hasLongMemory || attacker.getEntitySenses().canSee(currentTarget);

        if (canSeeTarget && --pathfindCooldown <= 0) {
            pathfindCooldown = BASE_PATHFIND_DELAY + attacker.getRNG().nextInt(PATHFIND_DELAY_VARIANCE);
            attacker.getNavigator().tryMoveToEntityLiving(currentTarget, moveSpeed);
        }
    }

    private void updateAttackCooldown() {
        attackCooldown = Math.max(attackCooldown - 1, 0);
    }

    private void attemptAttack() {
        // Don't attack entities riding the attacker
        if (currentTarget == attacker.riddenByEntity) {
            return;
        }

        if (!canReachTarget()) {
            return;
        }

        if (!attacker.getEntitySenses().canSee(currentTarget)) {
            return;
        }

        if (attackCooldown <= 0) {
            performAttack();
        }
    }

    private boolean canReachTarget() {
        double combinedWidth = attacker.width + currentTarget.width;
        double reachBonus = calculateReachBonus();
        double attackRangeSquared = (combinedWidth + reachBonus) * (combinedWidth + reachBonus);

        double distanceSquared = attacker.getDistanceSq(
                currentTarget.posX,
                currentTarget.boundingBox.minY,
                currentTarget.posZ
        );

        return distanceSquared <= attackRangeSquared;
    }

    private double calculateReachBonus() {
        ItemStack heldItem = attacker.getHeldItem();

        if (heldItem == null) {
            return 0.0D;
        }

        return heldItem.getItem().isItemTool(heldItem) ? TOOL_REACH_BONUS : 0.0D;
    }

    private void performAttack() {
        attackCooldown = ATTACK_COOLDOWN_TICKS;

        if (attacker.getHeldItem() != null) {
            attacker.swingItem();
        }

        attacker.attackEntityAsMob(currentTarget);
    }
}