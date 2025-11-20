package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityLiving;
import net.pottx.mobsenhancement.extend.EntityLivingExtend;
import net.pottx.mobsenhancement.mixin.access.EntityCreatureAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityLiving.class)
public class EntityLivingMixin implements EntityLivingExtend {
	@Unique EntityLiving self = (EntityLiving) (Object) this;

	public boolean mea$isWithinMaximumHomeDistance(int x, int y, int z) {
		if (!(self instanceof EntityCreature creature)) return true;
		EntityCreatureAccess access = (EntityCreatureAccess) creature;
		if (access.getMaximumHomeDistance() == -1.0F) {
			return true;
		} else {
			return access.getHomePosition().getDistanceSquared(x, y, z) < access.getMaximumHomeDistance() * access.getMaximumHomeDistance();
		}
	}
}
