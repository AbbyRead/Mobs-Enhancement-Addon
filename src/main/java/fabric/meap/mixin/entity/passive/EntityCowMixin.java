package fabric.meap.mixin.entity.passive;

import btw.entity.mob.KickingAnimal;
import btw.entity.mob.behavior.AnimalFleeBehavior;
import net.minecraft.src.*;
import btw.community.abbyread.meap.behavior.AnimalCombatBehavior;
import btw.community.abbyread.meap.extend.EntityLivingBaseExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityCow.class)
public abstract class EntityCowMixin extends KickingAnimal implements EntityLivingBaseExtend {

    @Unique
    private final EntityCow self = (EntityCow) (Object) this;

    @Unique
    private static final int IS_AGGRESSIVE_DATA_WATCHER_ID = 31;

    @SuppressWarnings("unused")
    private EntityCowMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "TAIL")
    )
    private void addExtraDataAndTasks(World world, CallbackInfo ci) {
        this.getDataWatcher().addObject(IS_AGGRESSIVE_DATA_WATCHER_ID, (byte)0);
        if (this.rand.nextInt(2) == 0) {
            this.setIsAggressive((byte)1);
        }

        this.tasks.removeAllTasksOfClass(AnimalFleeBehavior.class);
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);

        if (this.getIsAggressive() == (byte)1) {
            this.tasks.addTask(1, new AnimalCombatBehavior(self, 0.38F, 0.33F, EntityZombie.class, 4));
        } else {
            this.tasks.addTask(1, new AnimalCombatBehavior(self, 0.38F, 0.33F, EntityZombie.class, 15));
        }
    }

    @Unique
    public byte getIsAggressive() {
        return this.getDataWatcher().getWatchableObjectByte(IS_AGGRESSIVE_DATA_WATCHER_ID);
    }

    @Unique
    public void setIsAggressive(byte isAggressive) {
        this.getDataWatcher().updateObject(IS_AGGRESSIVE_DATA_WATCHER_ID, isAggressive);
    }

    @Inject(
            method = "writeEntityToNBT(Lnet/minecraft/src/NBTTagCompound;)V",
            at = @At("TAIL")
    )
    private void saveIsAggressive(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        par1NBTTagCompound.setByte("IsAggressive", this.getIsAggressive());
    }

    @Inject(
            method = "readEntityFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
            at = @At("TAIL")
    )
    private void loadIsAggressive(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        if (par1NBTTagCompound.hasKey("IsAggressive")) {
            this.setIsAggressive(par1NBTTagCompound.getByte("IsAggressive"));
        }
    }

}
