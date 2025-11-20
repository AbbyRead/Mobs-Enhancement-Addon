package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityGhast.class)
public abstract class EntityGhastMixin extends EntityFlying implements EntityGhastAccess {
    public EntityGhastMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void progressBasedExplosionStrength(CallbackInfo ci) {
        if (MEAUtils.getGameProgressMobsLevel(this.worldObj) > 1) {
            this.setExplosionStrength(2);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        if (!par1DamageSource.isMagicDamage() && !"fireball".equals(par1DamageSource.getDamageType())) {
            return false;
        } else {
            super.attackEntityFrom(par1DamageSource, par2);
        }
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        byte var1 = this.dataWatcher.getWatchableObjectByte(16);
        this.texture = var1 == 1 ? "/meatextures/ghast_fire.png" : "/meatextures/ghast.png";
    }

    @Override
    public int getMaxHealth() {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);

        return i > 1 ? 16 : 10;
    }
}
