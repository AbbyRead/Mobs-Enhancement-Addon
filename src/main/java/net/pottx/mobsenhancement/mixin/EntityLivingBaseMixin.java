package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
	@Unique EntityLivingBase self = (EntityLivingBase) (Object) this;

	// Affect player reputation among village on attacking villager
	@Inject(method = "attackEntityFrom", at = @At("HEAD"))
	private void overrideAttackEntityFrom(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!(self instanceof EntityVillager villager)) return;

		if (villager.villageObj != null && damageSource.getEntity() instanceof EntityPlayer player) {
			villager.villageObj.setReputationForPlayer(player.getCommandSenderName(), -1);
		}
	}
}
