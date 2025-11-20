package net.pottx.mobsenhancement.extend;

import net.minecraft.src.EntityEnderCrystal;

public interface EntityEnderCrystalExtend {
    void mea$setRespawnCounter(int respawnCounter);

    byte mea$getIsDried();

    void mea$setIsDried(byte isDried);

    EntityEnderCrystal mea$getChargingEnderCrystal();

    boolean mea$getIsOccupied();

    void mea$setIsOccupied(boolean isOccupied);

    boolean mea$getIsHealing();

    void mea$setIsHealing(boolean isHealing);
    
    void mea$setChargingCounter(int counter);

    int mea$getChargingCounter();
}
