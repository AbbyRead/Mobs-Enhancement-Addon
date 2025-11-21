package fabric.meap.mixin.entity.hostile;

import btw.community.abbyread.meap.ai.*;
import btw.community.abbyread.meap.behavior.SkeletonBreakTorchBehavior;
import btw.community.abbyread.meap.core.MEAUtils;
import com.llamalad7.mixinextras.sugar.Local;
import btw.community.abbyread.meap.extend.EntityArrowExtend;
import net.minecraft.src.*;
import btw.community.abbyread.meap.extend.EntityMobExtend;
import btw.community.abbyread.meap.extend.EntitySkeletonExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMob implements IRangedAttackMob, EntitySkeletonExtend {
    @Unique
    final
    EntitySkeleton self = (EntitySkeleton) (Object) this;

    @SuppressWarnings("unused")
    private EntitySkeletonMixin(World world) {
        super(world);
    }

    @Unique
    private EntityAISmartArrowAttack aiSmartRangedAttack;

    @Unique
    private EntityAISmartAttackOnCollide aiSmartMeleeAttack;

    @Unique
    private boolean isBreakingTorch;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addExtraTasks(CallbackInfo ci) {
        // Remove EntityAIFleeSun and EntityAIWatchClosest if they exist
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);
        this.tasks.removeAllTasksOfClass(EntityAIFleeSun.class);

        // Add custom behaviors
        tasks.addTask(2, new EntityAIFleeFromExplosion(this, 0.375F, 4.0F));
        tasks.addTask(3, new EntityAIFleeFromEnemy(this, EntityPlayer.class, 0.375F, 24.0F, 5));
        this.targetTasks.addTask(4, new SkeletonBreakTorchBehavior(self));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, 24.0F, 0, ((EntityMobExtend)this).meap$getCanXray() == (byte)0));
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void setSmartAttackAI(CallbackInfo ci) {
        this.aiSmartRangedAttack = new EntityAISmartArrowAttack(this, 0.375F, 60, 6, 20F, 6F);
        this.aiSmartMeleeAttack = new EntityAISmartAttackOnCollide(this, 0.375F, false, 6);
    }

    @Inject(
            method = "attackEntityWithRangedAttack(Lnet/minecraft/src/EntityLivingBase;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EnchantmentHelper;getEnchantmentLevel(ILnet/minecraft/src/ItemStack;)I", ordinal = 0)
    )
    private void resetArrowForPrediction(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, @Local EntityArrow arrow) {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        float f = i > 2 ? 2F : (i > 1 ? 4F : (i > 0 ? 6F : 8F));
        ((EntityArrowExtend)arrow).resetForPrediction(this, target, 1.6F, f);
    }

    @Inject(
            method = "attackEntityWithRangedAttack(Lnet/minecraft/src/EntityLivingBase;F)V",
            at = @At(value = "TAIL")
    )
    private void damageBow(EntityLivingBase target, float fDamageModifier, CallbackInfo ci) {
        ItemStack itemStack = this.getHeldItem();
        if (itemStack != null && itemStack.getItem() == Item.bow) {
            itemStack.damageItem(1, this);
            if (itemStack.stackSize <= 0) {
                // Replace the skeleton with one that has no bow
                EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                skeleton.setSkeletonType(this.self.getSkeletonType().id());
                skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                skeleton.setRotationYawHead(this.rotationYawHead);
                skeleton.setHealth(this.getHealth());
                skeleton.setCurrentItemOrArmor(0, null);
                this.setDead();
                this.worldObj.spawnEntityInWorld(skeleton);
            }
        }
    }

    // onSpawnWithEgg is called for natural spawns (not from a spawner block)
    @Inject(
            method = "onSpawnWithEgg(Lnet/minecraft/src/EntityLivingData;)Lnet/minecraft/src/EntityLivingData;",
            at = @At(value = "TAIL")
    )
    private void witherChanceAfterNether(EntityLivingData data, CallbackInfoReturnable<EntityLivingData> cir) {
        // Additional underground wither skeleton spawning logic
        if (this.worldObj.provider.dimensionId == 0 && this.posY < 32 && getRNG().nextInt(4) == 0) {
            self.setSkeletonType(1); // WITHER_TYPE
        }
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;removeTask(Lnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void removeSmartRangedAttackAI(Args args) {
        args.set(0, this.aiSmartRangedAttack);
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;removeTask(Lnet/minecraft/src/EntityAIBase;)V", ordinal = 0)
    )
    private void removeSmartMeleeAttackAI(Args args) {
        args.set(0, this.aiSmartMeleeAttack);
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 0)
    )
    private void addSmartRangedAttackAI(Args args) {
        args.set(1, this.aiSmartRangedAttack);
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void addSmartMeleeAttackAI(Args args) {
        args.set(1, this.aiSmartMeleeAttack);
    }

    @ModifyVariable(
            method = "dropFewItems(ZI)V",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    private int dropLessBones(int iNumBones, boolean bKilledByPlayer, int iLootingModifier) {
        return this.rand.nextInt(2 + iLootingModifier);
    }

    @Unique
    public boolean meap$getIsBreakingTorch() {
        return isBreakingTorch;
    }

    @Unique
    public void meap$setIsBreakingTorch(boolean isBreakingTorch) {
        this.isBreakingTorch = isBreakingTorch;
    }

    @Override
    public void addRandomArmor() {
        super.addRandomArmor();

        if (getHeldItem() != null && getHeldItem().itemID != Item.bow.itemID) {
            equipmentDropChances[0] = 0.99F;
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Redirect(
            method = "onLivingUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySkeleton;checkForCatchFireInSun()V")
    )
    private void skipSunCheck(EntitySkeleton EntitySkeleton) {
        // intentionally empty: skip the vanilla sun-catch-fire check for this mixin's behaviour
    }
}