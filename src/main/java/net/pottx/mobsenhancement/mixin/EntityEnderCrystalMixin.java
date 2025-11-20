package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.extend.EntityEnderCrystalExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityEnderCrystal.class)
public abstract class EntityEnderCrystalMixin extends Entity implements EntityEnderCrystalExtend {
    @Unique
    private static final int IS_DRIED_DATA_WATCHER_ID = 25;

    @Unique
    public EntityEnderCrystal chargingEnderCrystal;

    @Unique
    public boolean isOccupied;

    @Unique
    public boolean isHealing;

    @Unique
    private int chargingCounter;

    public EntityEnderCrystalMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "TAIL")
    )
    private void addIsDriedData(World world, CallbackInfo ci) {
        dataWatcher.addObject(IS_DRIED_DATA_WATCHER_ID, (byte) 0);
    }

    @Redirect(
            method = "attackEntityFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderCrystal;isEntityInvulnerable()Z")
    )
    private boolean doIsDriedCheck(EntityEnderCrystal entityEnderCrystal) {
        return ((EntityEnderCrystalExtend) entityEnderCrystal).mea$getIsDried() == (byte) 1 || entityEnderCrystal.isEntityInvulnerable();
    }

    @Redirect(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockId(III)I")
    )
    private int noFireIfDried(World world, int par1, int par2, int par3) {
        if (this.mea$getIsDried() == (byte) 1) {
            return Block.fire.blockID;
        } else {
            return this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        }
    }

    @Inject(
            method = "onUpdate",
            at = @At(value = "TAIL")
    )
    private void doChargeCycle(CallbackInfo ci) {
        if (this.mea$getIsDried() == (byte) 1 &&
                this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Block.fire.blockID) {
            this.worldObj.setBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
        }

        if (!this.worldObj.isRemote && this.mea$getIsDried() == (byte) 1 && this.chargingCounter >= 640) {
            this.mea$setIsDried((byte) 0);
        }

        if (this.chargingEnderCrystal != null) {
            if (this.mea$getIsDried() == (byte) 0 || ((EntityEnderCrystalExtend) this.chargingEnderCrystal).mea$getIsDried() == (byte) 1) {
                ((EntityEnderCrystalExtend) this.chargingEnderCrystal).mea$setIsOccupied(false);
                this.chargingEnderCrystal = null;
            } else if (this.chargingCounter < 640) {
                this.chargingCounter++;
            }
        }

        if (this.mea$getIsDried() == (byte) 1) {
            @SuppressWarnings("rawtypes")
            List nearCrystals = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand(32D, 32D, 32D));
            EntityEnderCrystal nearestChargerCrystal = null;
            double smallestDistance = Double.MAX_VALUE;

            for (Object nearCrystal : nearCrystals) {
                EntityEnderCrystal chargerCrystal = (EntityEnderCrystal) nearCrystal;
                double distance = chargerCrystal.getDistanceSqToEntity(this);

                if (((EntityEnderCrystalExtend) chargerCrystal).mea$getIsDried() == (byte) 0 &&
                        (chargerCrystal == this.chargingEnderCrystal || !((EntityEnderCrystalExtend) chargerCrystal).mea$getIsOccupied()) &&
                        !((EntityEnderCrystalExtend) chargerCrystal).mea$getIsHealing() &&
                        distance < smallestDistance) {
                    smallestDistance = distance;
                    nearestChargerCrystal = chargerCrystal;
                }
            }

            if (nearestChargerCrystal != this.chargingEnderCrystal) {
                if (this.chargingEnderCrystal != null) {
                    ((EntityEnderCrystalExtend) this.chargingEnderCrystal).mea$setIsOccupied(false);
                }
                if (nearestChargerCrystal != null) {
                    ((EntityEnderCrystalExtend) nearestChargerCrystal).mea$setIsOccupied(true);
                }

                this.chargingEnderCrystal = nearestChargerCrystal;
            }
        }
    }

    @Inject(
            method = "attackEntityFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderCrystal;setDead()V")
    )
    private void avengeDestroyer(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir) {
        Entity destroyer = par1DamageSource.getSourceOfDamage();
        if (destroyer instanceof EntityArrow) {
            destroyer = ((EntityArrow) destroyer).shootingEntity;
        } else if (destroyer instanceof EntityThrowable) {
            destroyer = ((EntityThrowable) destroyer).getThrower();
        }

        if (destroyer instanceof EntityLivingBase) {
            EntityLightningBolt lightningBolt = new EntityLightningBolt(this.worldObj, destroyer.posX, destroyer.posY, destroyer.posZ);
            this.worldObj.addWeatherEffect(lightningBolt);
        }
    }

    @Inject(
            method = "writeEntityToNBT(Lnet/minecraft/src/NBTTagCompound;)V",
            at = @At(value = "TAIL")
    )
    private void writeIsDried(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        par1NBTTagCompound.setByte("IsDried", this.mea$getIsDried());
        par1NBTTagCompound.setInteger("ChargingCounter", this.chargingCounter);
    }

    @Inject(
            method = "readEntityFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
            at = @At(value = "TAIL")
    )
    private void readIsDried(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        if (par1NBTTagCompound.hasKey("IsDried")) {
            this.mea$setIsDried(par1NBTTagCompound.getByte("IsDried"));
        }
        if (par1NBTTagCompound.hasKey("ChargingCounter")) {
            this.chargingCounter = par1NBTTagCompound.getInteger("ChargingCounter");
        }
    }

    @Override
    public void mea$setRespawnCounter(int respawnCounter) {
        this.chargingCounter = respawnCounter;
    }

    @Override
    public byte mea$getIsDried() {
        return this.dataWatcher.getWatchableObjectByte(IS_DRIED_DATA_WATCHER_ID);
    }

    @Override
    public void mea$setIsDried(byte isDried) {
        this.dataWatcher.updateObject(IS_DRIED_DATA_WATCHER_ID, isDried);
    }

    @Override
    public EntityEnderCrystal mea$getChargingEnderCrystal() {
        return this.chargingEnderCrystal;
    }

    @Override
    public boolean mea$getIsOccupied() {
        return this.isOccupied;
    }

    @Override
    public void mea$setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    @Override
    public boolean mea$getIsHealing() {
        return this.isHealing;
    }

    @Override
    public void mea$setIsHealing(boolean isHealing) {
        this.isHealing = isHealing;
    }

    @Override
    public void mea$setChargingCounter(int counter) {
        this.chargingCounter = counter;
    }

    @Override
    public int mea$getChargingCounter() {
        return this.chargingCounter;
    }
}