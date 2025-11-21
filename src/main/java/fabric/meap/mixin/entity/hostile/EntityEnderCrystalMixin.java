package fabric.meap.mixin.entity.hostile;

import net.minecraft.src.*;
import btw.community.abbyread.meap.extend.EntityEnderCrystalExtend;
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

    @SuppressWarnings("unused")
    private EntityEnderCrystalMixin(World par1World) {
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
        return ((EntityEnderCrystalExtend) entityEnderCrystal).meap$getIsDried() == (byte) 1 || entityEnderCrystal.isEntityInvulnerable();
    }

    @Redirect(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockId(III)I")
    )
    private int noFireIfDried(World world, int par1, int par2, int par3) {
        if (this.meap$getIsDried() == (byte) 1) {
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
        if (this.meap$getIsDried() == (byte) 1 &&
                this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Block.fire.blockID) {
            this.worldObj.setBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
        }

        if (!this.worldObj.isRemote && this.meap$getIsDried() == (byte) 1 && this.chargingCounter >= 640) {
            this.meap$setIsDried((byte) 0);
        }

        if (this.chargingEnderCrystal != null) {
            if (this.meap$getIsDried() == (byte) 0 || ((EntityEnderCrystalExtend) this.chargingEnderCrystal).meap$getIsDried() == (byte) 1) {
                ((EntityEnderCrystalExtend) this.chargingEnderCrystal).meap$setIsOccupied(false);
                this.chargingEnderCrystal = null;
            } else if (this.chargingCounter < 640) {
                this.chargingCounter++;
            }
        }

        if (this.meap$getIsDried() == (byte) 1) {
            @SuppressWarnings("rawtypes")
            List nearCrystals = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand(32D, 32D, 32D));
            EntityEnderCrystal nearestChargerCrystal = null;
            double smallestDistance = Double.MAX_VALUE;

            for (Object nearCrystal : nearCrystals) {
                EntityEnderCrystal chargerCrystal = (EntityEnderCrystal) nearCrystal;
                double distance = chargerCrystal.getDistanceSqToEntity(this);

                if (((EntityEnderCrystalExtend) chargerCrystal).meap$getIsDried() == (byte) 0 &&
                        (chargerCrystal == this.chargingEnderCrystal || !((EntityEnderCrystalExtend) chargerCrystal).meap$getIsOccupied()) &&
                        !((EntityEnderCrystalExtend) chargerCrystal).meap$getIsHealing() &&
                        distance < smallestDistance) {
                    smallestDistance = distance;
                    nearestChargerCrystal = chargerCrystal;
                }
            }

            if (nearestChargerCrystal != this.chargingEnderCrystal) {
                if (this.chargingEnderCrystal != null) {
                    ((EntityEnderCrystalExtend) this.chargingEnderCrystal).meap$setIsOccupied(false);
                }
                if (nearestChargerCrystal != null) {
                    ((EntityEnderCrystalExtend) nearestChargerCrystal).meap$setIsOccupied(true);
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
        par1NBTTagCompound.setByte("IsDried", this.meap$getIsDried());
        par1NBTTagCompound.setInteger("ChargingCounter", this.chargingCounter);
    }

    @Inject(
            method = "readEntityFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
            at = @At(value = "TAIL")
    )
    private void readIsDried(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        if (par1NBTTagCompound.hasKey("IsDried")) {
            this.meap$setIsDried(par1NBTTagCompound.getByte("IsDried"));
        }
        if (par1NBTTagCompound.hasKey("ChargingCounter")) {
            this.chargingCounter = par1NBTTagCompound.getInteger("ChargingCounter");
        }
    }

    @Override
    public void meap$setRespawnCounter(int respawnCounter) {
        this.chargingCounter = respawnCounter;
    }

    @Override
    public byte meap$getIsDried() {
        return this.dataWatcher.getWatchableObjectByte(IS_DRIED_DATA_WATCHER_ID);
    }

    @Override
    public void meap$setIsDried(byte isDried) {
        this.dataWatcher.updateObject(IS_DRIED_DATA_WATCHER_ID, isDried);
    }

    @Override
    public EntityEnderCrystal meap$getChargingEnderCrystal() {
        return this.chargingEnderCrystal;
    }

    @Override
    public boolean meap$getIsOccupied() {
        return this.isOccupied;
    }

    @Override
    public void meap$setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    @Override
    public boolean meap$getIsHealing() {
        return this.isHealing;
    }

    @Override
    public void meap$setIsHealing(boolean isHealing) {
        this.isHealing = isHealing;
    }

    @Override
    public void meap$setChargingCounter(int counter) {
        this.chargingCounter = counter;
    }

    @Override
    public int meap$getChargingCounter() {
        return this.chargingCounter;
    }
}