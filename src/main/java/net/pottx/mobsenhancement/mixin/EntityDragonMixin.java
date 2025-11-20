package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityEnderCrystalAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(EntityDragon.class)
public abstract class EntityDragonMixin extends EntityLiving
        implements IBossDisplayData,
        IEntityMultiPart,
        IMob {
    @Shadow public EntityEnderCrystal healingEnderCrystal;
    @Shadow public EntityDragonPart dragonPartHead;

    public EntityDragonMixin(World world) {
        super(world);
    }

    /**
     * @author Pot_Tx
     * @reason I didn't come up with a way to inject.
     */
    @Overwrite
    public void updateDragonEnderCrystal() {
        // Handle existing healing crystal
        if (this.healingEnderCrystal != null) {
            if (this.healingEnderCrystal.isDead) {
                if (!this.worldObj.isRemote) {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource(null), 10);
                }
                this.healingEnderCrystal = null;
            } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1);
            }
        }

        // Occasionally search for a new Ender Crystal to heal from
        if (this.rand.nextInt(10) == 0) {
            float searchRadius = 32.0F;

            @SuppressWarnings("unchecked")
            List<EntityEnderCrystal> nearbyCrystals = (List<EntityEnderCrystal>) (List<?>) this.worldObj.getEntitiesWithinAABB(
                    EntityEnderCrystal.class,
                    this.boundingBox.expand(searchRadius, searchRadius, searchRadius)
            );

            EntityEnderCrystal closestCrystal = null;
            double closestDistanceSq = Double.MAX_VALUE;

            // Find the nearest valid Ender Crystal
            for (EntityEnderCrystal crystal : nearbyCrystals) {
                double distanceSq = crystal.getDistanceSqToEntity(this);

                if (((EntityEnderCrystalAccess) crystal).getIsDried() == (byte) 0 && distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    closestCrystal = crystal;
                }
            }

            // Update healing crystal state
            if (closestCrystal != this.healingEnderCrystal) {
                if (this.healingEnderCrystal != null) {
                    ((EntityEnderCrystalAccess) this.healingEnderCrystal).setIsHealing(false);
                }
                if (closestCrystal != null) {
                    ((EntityEnderCrystalAccess) closestCrystal).setIsHealing(true);
                }
                this.healingEnderCrystal = closestCrystal;
            }
        }
    }

}
