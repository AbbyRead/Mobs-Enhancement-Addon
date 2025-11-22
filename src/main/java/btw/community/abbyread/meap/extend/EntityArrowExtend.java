package btw.community.abbyread.meap.extend;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;

public interface EntityArrowExtend {
    void resetForPrediction(EntityLiving owner, EntityLivingBase target, float arrowVelocity, float deviation);
}
