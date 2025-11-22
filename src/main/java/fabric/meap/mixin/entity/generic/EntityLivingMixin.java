package fabric.meap.mixin.entity.generic;

import net.minecraft.src.*;
import btw.community.abbyread.meap.core.MEAUtils;
import btw.community.abbyread.meap.extend.EntityLivingExtend;
import btw.community.abbyread.meap.extend.EntitySlimeExtend;
import fabric.meap.mixin.access.EntityCreatureAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public class EntityLivingMixin implements EntityLivingExtend {
	@Unique
	final EntityLiving self = (EntityLiving) (Object) this;

	public boolean meap$isWithinMaximumHomeDistance(int x, int y, int z) {
		if (!(self instanceof EntityCreature creature)) return true;
		EntityCreatureAccess access = (EntityCreatureAccess) creature;
		if (access.getMaximumHomeDistance() == -1.0F) {
			return true;
		} else {
			return access.getHomePosition().getDistanceSquared(x, y, z) < access.getMaximumHomeDistance() * access.getMaximumHomeDistance();
		}
	}

	// -----------------------------------------------
	// Override EntitySlime max HP to use MEA difficulty scaling
	// -----------------------------------------------
	@Inject(method = "applyEntityAttributes", at = @At("RETURN"))
	private void modifyMaxHealth(CallbackInfo ci) {
		if (!(self instanceof EntitySlime slime)) return;

		int tier = MEAUtils.getGameProgressMobsLevel(self.worldObj);
		tier = (tier > 0 ? 1 : 0);
		double base = slime.getSlimeSize() + tier;
		double maxHealth = base * base;

		self.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(maxHealth);
		self.setHealth((float) maxHealth);
	}

	// -------------------------------------------------------------------
	// Natural Spawn Core Assignment
	// -------------------------------------------------------------------
	// Only if NOT loaded from NBT
	@Inject(method = "onSpawnWithEgg", at = @At("TAIL"))
	private void afterSpawnWithEgg(EntityLivingData entityLivingData, CallbackInfoReturnable<EntityLivingData> cir) {
		if (!(self instanceof EntitySlime slime)) return;
		EntitySlimeExtend extended = (EntitySlimeExtend) slime;

		if (!extended.meap$getInitializedFromNBT() && slime.getSlimeSize() < 4) {
			if (self.rand.nextInt(4) == 0) {
				extended.meap$setIsCore((byte)1);
			}
		}
	}

}
