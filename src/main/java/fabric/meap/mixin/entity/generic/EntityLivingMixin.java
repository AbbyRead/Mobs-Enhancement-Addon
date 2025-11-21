package fabric.meap.mixin.entity.generic;

import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.SharedMonsterAttributes;
import btw.community.abbyread.meap.core.MEAUtils;
import btw.community.abbyread.meap.extension.EntityLivingExtend;
import fabric.meap.mixin.access.EntityCreatureAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		// Default init to normal intitialization value
		double baseHealth = self.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();

		if (self.worldObj != null) {
			int tier = MEAUtils.getGameProgressMobsLevel(self.worldObj);

			// Ignore higher difficulties for maxHealth calc
			tier = tier > 0 ? 1 : 0;

			baseHealth = slime.getSlimeSize() + tier;
		}

		self.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setAttribute(baseHealth);

		// Heal to new max HP since applyEntityAttributes is called at construction time
		self.setHealth((float) (baseHealth * baseHealth));
	}
}
