package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.extend.EntityLivingBaseExtend;
import net.pottx.mobsenhancement.extend.EntitySilverfishExtend;
import net.pottx.mobsenhancement.extend.EntityZombieExtend;
import net.pottx.mobsenhancement.mixin.access.EntityLivingBaseAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin implements EntityLivingBaseExtend {
	@Unique
	final EntityLivingBase self = (EntityLivingBase) (Object) this;

	@Inject(
			method = "canEntityBeSeen",
			at = @At(value = "RETURN"),
			cancellable = true
	)
	private void checkOtherPoints(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		boolean canTopBeSeen = self.worldObj.rayTraceBlocks_do_do(
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.height, entity.posZ ), false, true ) == null;

		boolean canCenterBeSeen = self.worldObj.rayTraceBlocks_do_do(
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + (entity.height / 2F), entity.posZ ), false, true ) == null;

		boolean canBottomBeSeen = self.worldObj.rayTraceBlocks_do_do(
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY, entity.posZ ), false, true ) == null;

		boolean canEyeBeSeen = self.worldObj.rayTraceBlocks_do_do(
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ ), false, true ) == null;

		cir.setReturnValue(canTopBeSeen || canCenterBeSeen || canBottomBeSeen || canEyeBeSeen);
	}

	public boolean meap$realisticCanEntityBeSeen(Entity entity, double absDist) {
		boolean canTopBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.height, entity.posZ ), false, true ) == null;

		boolean canCenterBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + (entity.height / 2F), entity.posZ ), false, true ) == null;

		boolean canBottomBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY, entity.posZ ), false, true ) == null;

		boolean canEyeBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ ), false, true ) == null;

		float yaw = self.rotationYawHead >= 0 ? self.rotationYawHead % 360 : self.rotationYawHead % 360 + 360;
		double angel = Math.atan2(entity.posX - self.posX, entity.posZ - self.posZ) * 180 / Math.PI;
		angel = angel >= 0 ? 360 - angel : 0 - angel;

		double realAbsDist = absDist;
		if (entity.isSneaking()) realAbsDist *= 0.5D;

		boolean isInSight = self.getDistanceSqToEntity(entity) < realAbsDist * realAbsDist || Math.abs(yaw - angel) < 75 || Math.abs(yaw - angel) > 185;

		return isInSight && (canTopBeSeen || canCenterBeSeen || canBottomBeSeen || canEyeBeSeen);
	}

	public boolean meap$realisticCanEntityBeSensed(Entity entity) {
		boolean canTopBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.height, entity.posZ ), false, true ) == null;

		boolean canCenterBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + (entity.height / 2F), entity.posZ ), false, true ) == null;

		boolean canBottomBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY, entity.posZ ), false, true ) == null;

		boolean canEyeBeSeen = MEAUtils.rayTraceBlocks_do_do_do( self.worldObj,
				self.worldObj.getWorldVec3Pool().getVecFromPool( self.posX, self.posY + (double)self.getEyeHeight(), self.posZ ),
				self.worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ ), false, true ) == null;

		return canTopBeSeen || canCenterBeSeen || canBottomBeSeen || canEyeBeSeen;
	}

	// Affect player reputation among village on attacking villager
	@Inject(method = "attackEntityFrom", at = @At("HEAD"))
	private void overrideAttackEntityFrom(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!(self instanceof EntityVillager villager)) return;

		if (villager.villageObj != null && damageSource.getEntity() instanceof EntityPlayer player) {
			villager.villageObj.setReputationForPlayer(player.getCommandSenderName(), -1);
		}
	}

	// Trigger silverfish splitting
	@Inject(method = "attackEntityFrom", at = @At(value = "RETURN", ordinal = 1))
	private void silverfishSplitWhenAttacked(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!(self instanceof EntitySilverfish silverfish)) return;
		if (damageSource instanceof EntityDamageSource && amount < silverfish.getHealth()) {
			((EntitySilverfishExtend)silverfish).meap$split();
		}
	}

	@Inject(method = "damageEntity", at = @At("HEAD"), cancellable = true)
	private void overrideDamageEntity(DamageSource damageSource, float amount, CallbackInfo ci) {
		EntityLivingBase self = (EntityLivingBase) (Object) this;

		// Only handle zombies
		if (!(self instanceof EntityZombie zombie)) return; // guard clause

		EntityZombieExtend extendedZombie = (EntityZombieExtend) zombie; // access an additional new method
		if (!self.isEntityInvulnerable())
		{
			if (damageSource == DamageSource.onFire && !zombie.isVillager() && self.getHealth() <= amount)
			{
				extendedZombie.meap$onKilledBySun();
			}
			else
			{
				((EntityLivingBaseAccess)self).invokeDamageEntity(damageSource, amount);
			}
		}
		// Cancel normal handling for EntityZombie's inherited damageEntity
		ci.cancel();
	}


}
