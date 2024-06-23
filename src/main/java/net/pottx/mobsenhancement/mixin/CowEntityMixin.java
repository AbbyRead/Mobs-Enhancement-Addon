package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.CowEntity;
import btw.entity.mob.ZombieEntity;
import btw.entity.mob.behavior.AnimalFleeBehavior;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.AnimalCombatBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(CowEntity.class)
public class CowEntityMixin extends EntityCow {
    @Unique
    private static final int IS_AGGRESSIVE_DATA_WATCHER_ID = 31;

    public CowEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public int getMeleeAttackStrength(Entity target) {
        return 4;
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addExtraDataAndTasks(CallbackInfo ci) {
        dataWatcher.addObject(IS_AGGRESSIVE_DATA_WATCHER_ID, (byte)0);
        if (this.rand.nextInt(2) == 0) this.setIsAggressive((byte)1);

        this.tasks.removeAllTasksOfClass(AnimalFleeBehavior.class);
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);

        if (this.getIsAggressive() == (byte)1) {
            tasks.addTask(1, new AnimalCombatBehavior(this, 0.38F, 0.33F, ZombieEntity.class, 4));
        } else {
            tasks.addTask(1, new AnimalCombatBehavior(this, 0.38F, 0.33F, ZombieEntity.class, 15));
        }
    }

    @Unique
    public byte getIsAggressive() {
        return this.dataWatcher.getWatchableObjectByte(IS_AGGRESSIVE_DATA_WATCHER_ID);
    }

    @Unique
    public void setIsAggressive(byte isAggressive) {
        this.dataWatcher.updateObject(IS_AGGRESSIVE_DATA_WATCHER_ID, isAggressive);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setByte("IsAggressive", this.getIsAggressive());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);

        this.setIsAggressive(par1NBTTagCompound.getByte("IsAggressive"));
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        Entity attacker = par1DamageSource.getEntity();
        if (attacker instanceof EntityLiving) {
            this.setAttackTarget((EntityLiving)attacker);

            List animalList = worldObj.getEntitiesWithinAABB( EntityAnimal.class, boundingBox.expand( 24D, 12D, 24D ) );

            Iterator itemIterator = animalList.iterator();

            while (itemIterator.hasNext())
            {
                EntityAnimal tempAnimal = (EntityAnimal)itemIterator.next();

                boolean isSpeciesSame = tempAnimal instanceof CowEntity;

                if (!tempAnimal.isLivingDead && isSpeciesSame && !tempAnimal.hasAttackTarget() && tempAnimal.canEntityBeSeen(this))
                {
                    tempAnimal.setAttackTarget((EntityLiving)attacker);
                }

            }
        }

        return super.attackEntityFrom(par1DamageSource, par2);
    }
}
