package net.pottx.mobsenhancement.access;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;

public interface EntityArrowAccess {
    void resetForPrediction(EntityLiving owner, EntityLivingBase target, float arrowVelocity, float deviation);
}
