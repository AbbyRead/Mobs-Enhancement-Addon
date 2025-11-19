package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.behavior.AnimalFleeBehavior;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.AnimalCombatBehavior;
import net.pottx.mobsenhancement.EntityAIFleeFromExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(EntityPig.class)
public abstract class EntityPigMixin extends EntityAnimal {
    public EntityPigMixin(World par1World) {
        super(par1World);
    }

    @Override
    public int getMeleeAttackStrength(Entity target) {
        return 3;
    }


    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addExtraTasks(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(AnimalFleeBehavior.class);
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);

        tasks.addTask(1, new AnimalCombatBehavior(this, 0.33F, 0.38F, EntityZombie.class, 6));
        tasks.addTask(2, new EntityAIFleeFromExplosion(this, 0.38F, 4.0F));
    }

    @Inject(
            method = "attackEntityFrom",
            at = @At(value = "RETURN")
    )
    private void setAttackTargetWhenAttacked(DamageSource par1DamageSource, int par2, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = par1DamageSource.getEntity();
        if (attacker instanceof EntityLiving) {
            this.setAttackTarget((EntityLiving)attacker);

            List animalList = worldObj.getEntitiesWithinAABB( EntityAnimal.class, boundingBox.expand( 24D, 12D, 24D ) );

            Iterator itemIterator = animalList.iterator();

            while (itemIterator.hasNext())
            {
                EntityAnimal tempAnimal = (EntityAnimal)itemIterator.next();

                boolean isSpeciesSame = tempAnimal instanceof PigEntity;

                if (!tempAnimal.isLivingDead && isSpeciesSame && !tempAnimal.hasAttackTarget() && tempAnimal.canEntityBeSeen(this))
                {
                    tempAnimal.setAttackTarget((EntityLiving)attacker);
                }

            }
        }
    }
}
