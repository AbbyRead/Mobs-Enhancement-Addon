package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable {

    @Unique
    private EntityAnimal self = (EntityAnimal) (Object) this;

    @Unique
    private int pushedCounter = 0;

    public EntityAnimalMixin(World world) {
        super(world);
    }

    @Inject(
            method = "onLivingUpdate",
            at = @At(value = "TAIL")
    )
    private void updatePushed(CallbackInfo ci) {
        EntityPlayer player = (EntityPlayer) this.worldObj.findNearestEntityWithinAABB(EntityPlayer.class, this.boundingBox.expand(1.5D, 1D, 1.5D), this);
        if (player != null && this.boundingBox.expand(0.35D, 0.35D, 0.35D).intersectsWith(player.boundingBox)) {
            if (this.pushedCounter < 10) {
                ++this.pushedCounter;
            } else {
                this.setRevengeTarget(player);
            }
        } else if (this.pushedCounter > 0) {
            this.pushedCounter = 0;
        }
    }

    // EntityCow-specific inject
    @Inject(
            method = "attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z",
            at = @At("HEAD")
    )
    private void setAggroOnAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        // Only proceed if this is actually a cow
        if (!(self instanceof EntityCow)) {
            return;
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof EntityLivingBase) {
            // Set the attack target for this cow
            self.setAttackTarget((EntityLivingBase) attacker);

            // Find nearby animals
            @SuppressWarnings("unchecked")
            List<EntityAnimal> animalList = this.worldObj.getEntitiesWithinAABB(
                    EntityAnimal.class,
                    this.boundingBox.expand(24D, 12D, 24D)
            );

            for (EntityAnimal tempAnimal : animalList) {
                boolean isSameSpecies = tempAnimal instanceof EntityCow;

                if (!tempAnimal.isLivingDead
                        && isSameSpecies
                        && tempAnimal.getAttackTarget() == null
                        && tempAnimal.canEntityBeSeen(self)) {
                    tempAnimal.setAttackTarget((EntityLivingBase) attacker);
                }
            }
        }
    }
}
