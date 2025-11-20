package net.pottx.mobsenhancement.mixin.access;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface EntityLivingBaseAccess {
	@Invoker("damageEntity")
	void invokeDamageEntity(DamageSource damageSource, float amount);
}
