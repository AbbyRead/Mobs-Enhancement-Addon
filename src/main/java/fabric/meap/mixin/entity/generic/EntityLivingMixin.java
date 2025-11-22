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

		// Determine MEA difficulty tier (0 or 1)
		int tier = 0;
		if (self.worldObj != null) {
			tier = MEAUtils.getGameProgressMobsLevel(self.worldObj);
			tier = (tier > 0 ? 1 : 0); // MEA only uses tier 0 or 1 for slimes
		}

		// MEA formula: (size + tier)^2
		double base = slime.getSlimeSize() + tier;
		double maxHealth = base * base;

		// Apply to attribute
		self.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setAttribute(maxHealth);

		// Heal to full, because applyEntityAttributes runs during construction,
		// and increasing maxHealth does not automatically raise current health.
		self.setHealth((float) maxHealth);
	}

	// -------------------------------------------------------------------
	// Slime natural spawn core assignment
	// ONLY if NOT loaded from NBT
	// -------------------------------------------------------------------

	@Inject(method = "onSpawnWithEgg", at = @At("TAIL"))
	private void afterSpawnWithEgg(EntityLivingData entityLivingData, CallbackInfoReturnable<EntityLivingData> cir) {
		if (!(self instanceof EntitySlime slime)) return;

		EntitySlimeExtend extendedSlime = (EntitySlimeExtend) slime;
		if (!extendedSlime.meap$getInitializedFromNBT() && slime.getSlimeSize() < 4) {
			// 1 in 4 chance
			if (self.rand.nextInt(4) == 0) {
				extendedSlime.meap$setIsCore((byte)1);
			}
		}
	}

}
