package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityEnderCrystal;
import net.pottx.mobsenhancement.extend.EntityEnderCrystalExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

	@Unique
	final Entity self = (Entity) (Object) this;

	// Prevent ender crystal burning
	@Inject(method = "isBurning", at = @At("HEAD"), cancellable = true)
	private void neverBurning(CallbackInfoReturnable<Boolean> cir) {
		if (!(self instanceof EntityEnderCrystal)) return;
		cir.setReturnValue(false);
	}

	@Inject(method = "setDead", at = @At("HEAD"), cancellable = true)
	private void preventDeathIfDried(CallbackInfo ci) {
		if (!(self instanceof EntityEnderCrystal)) return;

		EntityEnderCrystalExtend crystal = (EntityEnderCrystalExtend) self;
		if (crystal.mea$getIsDried() == (byte) 0) {
			// Allow normal death
			crystal.mea$setIsDried((byte) 1);
			crystal.mea$setChargingCounter(0);
		} else {
			// Already dried, don't actually die
			ci.cancel();
		}
	}

}
