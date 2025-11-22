package fabric.meap.mixin.entity.hostile;

import btw.community.abbyread.meap.extend.EntityWitherExtend;
import fabric.meap.mixin.access.EntityWitherAccess;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import btw.community.abbyread.meap.behavior.WitherDashBehavior;
import btw.community.abbyread.meap.behavior.WitherSummonMinionBehavior;
import java.util.List;

import btw.block.BTWBlocks;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob implements IBossDisplayData,
        IRangedAttackMob, EntityWitherExtend, EntityWitherAccess {

    @Unique private final EntityWither self = (EntityWither)(Object)this;
    @Unique public boolean isDoingSpecialAttack;

    public EntityWitherMixin(World world) {
        super(world);
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
    public boolean meap$getIsDoingSpecialAttack() {
        return this.isDoingSpecialAttack;
    }

    @Override
    public void meap$setIsDoingSpecialAttack(boolean isDoingSpecialAttack) {
        this.isDoingSpecialAttack = isDoingSpecialAttack;
    }

    @Override
    public int getMeleeAttackStrength(Entity target) {
        return 10;
    }

    @Override
    public void updateAITasks() {
        int countdown;

        if (this.meap$getSpawnInvulnerabilityTime() > 0) {
            countdown = this.meap$getSpawnInvulnerabilityTime() - 1;

            if (countdown <= 0) {
                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
                this.worldObj.func_82739_e(1013, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            }

            this.meap$setSpawnInvulnerabilityTime(countdown);

            if (this.ticksExisted % 10 == 0) {
                this.heal(10);
            }
        } else {
            super.updateAITasks();

            for (int headIndex = 1; headIndex < 3; ++headIndex) {
                if (!this.meap$getIsDoingSpecialAttack() && this.ticksExisted >= this.meap$getNextHeadAttackTime()[headIndex - 1]) {
                    this.meap$getNextHeadAttackTime()[headIndex - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);

                    int previousAttackCount = this.meap$getIdleHeadTicks()[headIndex - 1];
                    this.meap$getIdleHeadTicks()[headIndex - 1] = previousAttackCount + 1;

                    if (previousAttackCount > 15) {
                        float xOffset = 10.0F;
                        float yOffset = 5.0F;
                        double targetX = MathHelper.getRandomDoubleInRange(this.rand, this.posX - (double)xOffset, this.posX + (double)xOffset);
                        double targetY = MathHelper.getRandomDoubleInRange(this.rand, this.posY - (double)yOffset, this.posY + (double)yOffset);
                        double targetZ = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - (double)xOffset, this.posZ + (double)xOffset);
                        this.meap$shootSkullAt(headIndex + 1, targetX, targetY, targetZ, true);
                        this.meap$getIdleHeadTicks()[headIndex - 1] = 0;
                    }

                    int watchedTargetId = self.getWatchedTargetId(headIndex);

                    if (watchedTargetId > 0) {
                        Entity watchedEntity = this.worldObj.getEntityByID(watchedTargetId);

                        if (watchedEntity != null && watchedEntity.isEntityAlive() && this.getDistanceSqToEntity(watchedEntity) <= 900.0D && this.canEntityBeSeen(watchedEntity)) {
                            this.meap$fireSkullAtEntity(headIndex + 1, (EntityLiving)watchedEntity);
                            this.meap$getNextHeadAttackTime()[headIndex - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                            this.meap$getIdleHeadTicks()[headIndex - 1] = 0;
                        } else {
                            this.meap$setHeadTarget(headIndex, 0);
                        }
                    } else {
                        @SuppressWarnings("unchecked") 
                        List<EntityLivingBase> nearbyEntities = this.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(20.0D, 8.0D, 20.0D), EntityWitherAccess.meap$getAttackEntitySelector());

                        for (int attempt = 0; attempt < 10 && !nearbyEntities.isEmpty(); ++attempt) {
                            EntityLivingBase potentialTarget = nearbyEntities.get(this.rand.nextInt(nearbyEntities.size()));

                            if (potentialTarget != this && potentialTarget.isEntityAlive() && this.canEntityBeSeen(potentialTarget)) {
                                if (potentialTarget instanceof EntityPlayer player) {
                                    if (!player.capabilities.disableDamage) {
                                        this.meap$setHeadTarget(headIndex, potentialTarget.entityId);
                                    }
                                } else {
                                    this.meap$setHeadTarget(headIndex, potentialTarget.entityId);
                                }
                                break;
                            }

                            nearbyEntities.remove(potentialTarget);
                        }
                    }
                }
            }

            if (this.getAttackTarget() != null) {
                this.meap$setHeadTarget(0, this.getAttackTarget().entityId);
            } else {
                this.meap$setHeadTarget(0, 0);
            }

            if (!this.meap$getIsDoingSpecialAttack() && this.meap$getBlockBreakCounter() > 0) {
                this.meap$setBlockBreakCounter(this.meap$getBlockBreakCounter() - 1);

                if (this.meap$getBlockBreakCounter() == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                    int baseY = MathHelper.floor_double(this.posY);
                    int baseX = MathHelper.floor_double(this.posX);
                    int baseZ = MathHelper.floor_double(this.posZ);
                    boolean destroyedAnyBlocks = false;

                    for (int xOffset = -1; xOffset <= 1; ++xOffset) {
                        for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                            for (int yOffset = 0; yOffset <= 3; ++yOffset) {
                                int blockX = baseX + xOffset;
                                int blockY = baseY + yOffset;
                                int blockZ = baseZ + zOffset;
                                int blockId = this.worldObj.getBlockId(blockX, blockY, blockZ);

                                if (blockId > 0 && blockId != Block.bedrock.blockID && blockId != Block.endPortal.blockID && blockId != Block.endPortalFrame.blockID &&
                                        blockId != BTWBlocks.soulforgedSteelBlock.blockID) {
                                    destroyedAnyBlocks = this.worldObj.destroyBlock(blockX, blockY, blockZ, true) || destroyedAnyBlocks;
                                }
                            }
                        }
                    }

                    if (destroyedAnyBlocks) {
                        this.worldObj.playAuxSFXAtEntity(null, 1012, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    }
                }
            }

            if (this.ticksExisted % 20 == 0) {
                this.heal(1);
            }
        }
    }

    @Override
    public boolean meleeAttack(Entity target) {
        self.setLastAttacker(target);

        int attackStrength = getMeleeAttackStrength(target);

        if (isPotionActive(Potion.damageBoost)) {
            attackStrength += 3 << getActivePotionEffect(Potion.damageBoost).getAmplifier();
        }

        if (isPotionActive(Potion.weakness)) {
            attackStrength -= 2 << getActivePotionEffect(Potion.weakness).getAmplifier();
        }

        int knockback = 2;

        boolean attackSuccess = target.attackEntityFrom(DamageSource.causeMobDamage(this), attackStrength);

        if (attackSuccess) {
            target.addVelocity(
                    -MathHelper.sin(rotationYaw * (float)Math.PI / 180F) * knockback * 0.5F,
                    0.1D,
                    MathHelper.cos(rotationYaw * (float)Math.PI / 180F) * knockback * 0.5F
            );

            motionX *= 0.6D;
            motionZ *= 0.6D;

            int fireModifier = EnchantmentHelper.getFireAspectModifier(this);

            if (fireModifier > 0) {
                target.setFire(fireModifier * 4);
            } else if (isBurning() && rand.nextFloat() < 0.6F) {
                target.setFire(4);
            }
        }

        return attackSuccess;
    }
}
