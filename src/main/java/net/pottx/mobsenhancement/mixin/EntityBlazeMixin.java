package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityBlaze;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBlaze.class)
public abstract class EntityBlazeMixin extends EntityLivingBase {

    private EntityBlazeMixin(World world) {
        super(world);
    }

    @Inject(method = "applyEntityAttributes", at = @At("RETURN"))
    private void modifyMaxHealth(CallbackInfo ci) {
        double baseHp = 20.0;

        if (this.worldObj != null) {
            int tier = MEAUtils.getGameProgressMobsLevel(this.worldObj);
            if (tier > 1) {
                baseHp = 24.0;
            }
        }

        // Set new max HP
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setAttribute(baseHp);

        // Heal Blaze to its new max HP since the super.applyEntityAttributes
        //   call may have set its health lower, and applyEntityAttributes is
        //   only used at construction time of the entity.
        this.setHealth((float) baseHp);
    }
}