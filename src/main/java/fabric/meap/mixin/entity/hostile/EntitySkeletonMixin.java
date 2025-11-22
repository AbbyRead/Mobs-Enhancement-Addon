package fabric.meap.mixin.entity.hostile;

import btw.community.abbyread.meap.ai.*;
import btw.community.abbyread.meap.behavior.SkeletonBreakTorchBehavior;
import btw.community.abbyread.meap.extend.EntitySkeletonExtend;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMob implements IRangedAttackMob, EntitySkeletonExtend {
    @Unique
    final EntitySkeleton self = (EntitySkeleton) (Object) this;

    // ----------------------------------------------------------------
    // Unique fields for custom AI / behavior
    // ----------------------------------------------------------------
    @Unique
    private boolean isBreakingTorch;

    @Unique
    private boolean canXray = true; // replaces EntityMobExtend cast

    @SuppressWarnings("FieldCanBeLocal")
    @Unique
    private EntityAISmartArrowAttack aiSmartRangedAttack;

    @SuppressWarnings("FieldCanBeLocal")
    @Unique
    private EntityAISmartAttackOnCollide aiSmartMeleeAttack;

    public EntitySkeletonMixin(World par1World) {
        super(par1World);
    }

    // ----------------------------------------------------------------
    // Getter / Setter for breaking torch
    // ----------------------------------------------------------------
    @Override
    public boolean meap$getIsBreakingTorch() {
        return isBreakingTorch;
    }

    @Override
    public void meap$setIsBreakingTorch(boolean value) {
        this.isBreakingTorch = value;
    }

    // ----------------------------------------------------------------
    // Getter / Setter for canXray
    // ----------------------------------------------------------------
    @Override
    public boolean meap$getCanXray() {
        return canXray;
    }

    @Override
    public void meap$setCanXray(boolean value) {
        this.canXray = value;
    }

    // ----------------------------------------------------------------
    // AI setup (called after spawning via onSpawnWithEgg)
    // ----------------------------------------------------------------
    @Inject(
            method = "onSpawnWithEgg(Lnet/minecraft/src/EntityLivingData;)Lnet/minecraft/src/EntityLivingData;",
            at = @At("TAIL")
    )
    private void initMixinAI(EntityLivingData data, CallbackInfoReturnable<EntityLivingData> cir) {
        // Remove vanilla tasks that conflict with our AI
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);
        this.tasks.removeAllTasksOfClass(EntityAIFleeSun.class);

        // Add custom tasks
        this.tasks.addTask(2, new EntityAIFleeFromExplosion(this, 0.375F, 4.0F));
        this.tasks.addTask(3, new EntityAIFleeFromEnemy(this, EntityPlayer.class, 0.375F, 24.0F, 5));

        this.targetTasks.addTask(4, new SkeletonBreakTorchBehavior(self));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, 24.0F, 0, !this.canXray));

        // Setup smart attack AI
        this.aiSmartRangedAttack = new EntityAISmartArrowAttack(this, 0.375F, 60, 6, 20F, 6F);
        this.aiSmartMeleeAttack = new EntityAISmartAttackOnCollide(this, 0.375F, false, 6);
    }
}
