package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySpider.class)
public abstract class EntitySpiderMixin extends EntityMob
{
    @SuppressWarnings("unused")
    private EntitySpiderMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addNaturalPotion(CallbackInfo ci) {
        if ((this.worldObj == null ? 0 : MEAUtils.getGameProgressMobsLevel(this.worldObj)) > 0) {
            int i = this.rand.nextInt(16);

            if (i == 0) {
                this.addPotionEffect(new PotionEffect(1, Integer.MAX_VALUE));
            } else if (i == 1) {
                this.addPotionEffect(new PotionEffect(5, Integer.MAX_VALUE));
            } else if (i == 2) {
                this.addPotionEffect(new PotionEffect(14, Integer.MAX_VALUE));
            }
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("RETURN"))
    private void modifyMaxHealth(CallbackInfo ci) {
        double baseHealth = 16.0;

        if (this.worldObj != null) {
            int tier = MEAUtils.getGameProgressMobsLevel(this.worldObj);
            if (tier > 0) {
                baseHealth = 20.0;
            }
        }

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setAttribute(baseHealth);

        // Heal to new max HP since applyEntityAttributes is called at construction time
        this.setHealth((float) baseHealth);
    }
}
