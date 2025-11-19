package net.pottx.mobsenhancement.access;

import net.minecraft.src.EntityLiving;

public interface EntityArrowAccess {
    void mea$resetForPrediction(EntityLiving owner, EntityLiving target, float arrowVelocity, float deviation);
}
