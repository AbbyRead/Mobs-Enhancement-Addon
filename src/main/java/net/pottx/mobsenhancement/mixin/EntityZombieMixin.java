package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAIBreakBlock;
import net.pottx.mobsenhancement.EntityAISmartAttackOnCollide;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.extend.EntityMobExtend;
import net.pottx.mobsenhancement.extend.EntityZombieExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityZombie.class)
public abstract class EntityZombieMixin extends EntityMob implements EntityZombieExtend {

    @Unique
    private boolean isBreakingBlock = false;
    @Shadow
    private IEntitySelector targetEntitySelector;

    public EntityZombieMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addBreakBlockTask(CallbackInfo ci) {
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);
        this.tasks.removeAllTasksOfClass(EntityAIAttackOnCollide.class);

        this.tasks.addTask(1, new EntityAIBreakBlock(this));
        this.tasks.addTask(2, new EntityAISmartAttackOnCollide(this, this.getSpeedModifier(), false, 0));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(
                this, EntityPlayer.class, 24.0F, 0,
                ((EntityMobExtend)this).getCanXray() == (byte)0
        ));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 24.0F, 0, false));this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(
                this,
                EntityCreature.class,
                24,       // integer instead of float
                false,    // par4
                false,    // par5
                targetEntitySelector, // par6
                false     // ignoreOutsideHome
        ));
    }

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void replaceAttackOnCollideTask(Args args) {
        args.set(1, new EntityAISmartAttackOnCollide(this, this.getSpeedModifier(), false, 0));
    }

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 5)
    )
    private void modifyNearestAttackableVillagerTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(
                this,                     // owner
                EntityCreature.class,      // target class
                24,                        // chance (int)
                false,                     // checkSight
                false,                     // nearbyOnly
                targetEntitySelector,      // selector
                false                      // ignoreOutsideHome
        ));
    }

    @Unique
    public boolean mea$getIsBreakingBlock() {
        return this.isBreakingBlock;
    }

    @Unique
    public void mea$setIsBreakingBlock(boolean isBreakingBlock) {
        this.isBreakingBlock = isBreakingBlock;
    }

    public void mea$onKilledBySun() {
        if (this.worldObj.isRemote) return;

        // Create the skeleton directly
        EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);

        // Position it where the zombie died
        skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

        // Reduce health so it starts at half
        skeleton.setHealth(MathHelper.ceiling_float_int(skeleton.getMaxHealth() / 2.0F));

        // Copy armor / held items from the zombie
        for (int i = 0; i < 5; i++) {
            skeleton.setCurrentItemOrArmor(i, this.getCurrentItemOrArmor(i));
        }

        // Call vanilla creature initialization (spawnerInitCreature)
        skeleton.spawnerInitCreature();

        // Spawn in the world
        this.worldObj.spawnEntityInWorld(skeleton);

        // Remove the zombie
        this.setDead();
    }

    @Inject(method = "applyEntityAttributes", at = @At("RETURN"))
    private void modifyMaxHealth(CallbackInfo ci) {
        int tier = this.worldObj != null ? MEAUtils.getGameProgressMobsLevel(this.worldObj) : 0;

        double baseHealth = switch (tier) {
            case 0 -> 16.0;
            case 1 -> 20.0;
            default -> 24.0; // assuming tier 2 or higher
        };

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(baseHealth);
        this.setHealth((float) baseHealth);
    }

}
