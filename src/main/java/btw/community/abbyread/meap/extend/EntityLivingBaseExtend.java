package btw.community.abbyread.meap.extend;

import net.minecraft.src.Entity;

public interface EntityLivingBaseExtend {
	boolean meap$realisticCanEntityBeSeen(Entity entity, double absDist);
	boolean meap$realisticCanEntityBeSensed(Entity entity);
}
