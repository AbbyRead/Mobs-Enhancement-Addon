package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable {
    @Shadow public abstract void setRevengeTarget(EntityLiving targetEntity);

    @Unique
    private int pushedCounter = 0;

    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "onLivingUpdate",
            at = @At(value = "TAIL")
    )
    private void updatePushed(CallbackInfo ci) {
        EntityPlayer player = (EntityPlayer) this.worldObj.findNearestEntityWithinAABB(EntityPlayer.class, this.boundingBox.expand(1.5D, 1D, 1.5D), this);
        if (player != null && this.boundingBox.expand(0.35D, 0.35D, 0.35D).intersectsWith(player.boundingBox)) {
            if (this.pushedCounter < 10) {
                ++this.pushedCounter;
            } else {
                this.setRevengeTarget(player);
            }
        } else if (this.pushedCounter > 0) {
            this.pushedCounter = 0;
        }

    }
}
