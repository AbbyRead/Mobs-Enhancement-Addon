package btw.community.abbyread.meap.extend;

import net.minecraft.src.EntityEnderCrystal;

public interface EntityEnderCrystalExtend {
    @SuppressWarnings("unused")
    void meap$setRespawnCounter(int respawnCounter);

    byte meap$getIsDried();

    void meap$setIsDried(byte isDried);

    EntityEnderCrystal meap$getChargingEnderCrystal();

    boolean meap$getIsOccupied();

    void meap$setIsOccupied(boolean isOccupied);

    boolean meap$getIsHealing();

    void meap$setIsHealing(boolean isHealing);
    
    void meap$setChargingCounter(int counter);

	@SuppressWarnings("unused")
	default int meap$getChargingCounter() {
		return 0;
	}
}
