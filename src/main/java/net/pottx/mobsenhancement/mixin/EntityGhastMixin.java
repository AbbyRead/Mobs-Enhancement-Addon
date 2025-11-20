package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityGhast.class)
public abstract class EntityGhastMixin extends EntityFlying implements EntityGhastAccess {

    public EntityGhastMixin(World world) {
        super(world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setProgressBasedExplosionStrength(World world, CallbackInfo ci) {
        if (MEAUtils.getGameProgressMobsLevel(this.worldObj) > 1) {
            this.setExplosionStrength(2);
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("RETURN"))
    private void modifyMaxHealth(CallbackInfo ci) {
        double baseHealth = 10.0;

        if (this.worldObj != null) {
            int tier = MEAUtils.getGameProgressMobsLevel(this.worldObj);
            if (tier > 1) {
                baseHealth = 16.0;
            }
        }

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setAttribute(baseHealth);

        // Heal to new max HP since applyEntityAttributes is called at construction time
        this.setHealth((float) baseHealth);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void restrictDamageTypes(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Only allow magic damage and fireball damage
        if (!damageSource.isMagicDamage() && !"fireball".equals(damageSource.getDamageType())) {
            cir.setReturnValue(false);
        }
    }
}