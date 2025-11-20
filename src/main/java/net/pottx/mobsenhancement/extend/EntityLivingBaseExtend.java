package net.pottx.mobsenhancement.extend;

import net.minecraft.src.Entity;

public interface EntityLivingBaseExtend {
	boolean mea$realisticCanEntityBeSeen(Entity entity, double absDist);
	boolean mea$realisticCanEntityBeSensed(Entity entity);
}
