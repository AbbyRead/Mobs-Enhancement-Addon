package btw.community.abbyread.meap.extension;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;

public interface EntityArrowExtend {
    void resetForPrediction(EntityLiving owner, EntityLivingBase target, float arrowVelocity, float deviation);
}
