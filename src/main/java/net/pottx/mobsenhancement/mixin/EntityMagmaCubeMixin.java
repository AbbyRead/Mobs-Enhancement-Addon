package net.pottx.mobsenhancement.mixin;

import net.pottx.mobsenhancement.MEAUtils;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.extend.EntitySlimeExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMagmaCube.class)
public abstract class EntityMagmaCubeMixin extends EntitySlime {
    private EntityMagmaCubeMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void setIsMagma(CallbackInfo ci) {
        ((EntitySlimeExtend)this).mea$setIsMagma(true);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava)) {
            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 10, 2, true));
        }
    }

    @Override
    public void setDead() {
        if (this.getSlimeSize() == 1) {
            World world = this.worldObj;

            int lavaPosX = MathHelper.floor_double(this.posX);
            int lavaPosY = MathHelper.floor_double(this.posY);
            int lavaPosZ = MathHelper.floor_double(this.posZ);

            boolean canPlaceLava = world.isAirBlock(lavaPosX, lavaPosY, lavaPosZ);

            if (canPlaceLava) {
                MEAUtils.placeNonPersistentLava(world, lavaPosX, lavaPosY, lavaPosZ);
            }
        }
        super.setDead();
    }

    @Inject(method = "applyEntityAttributes", at = @At("RETURN"))
    private void modifyMaxHealth(CallbackInfo ci) {
        // Default init to normal intitialization value
        double baseHealth = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();

        if (this.worldObj != null) {
            int tier = MEAUtils.getGameProgressMobsLevel(this.worldObj);

            // Ignore higher difficulties for maxHealth calc
            tier = tier > 1 ? 1 : 0;

            baseHealth = this.getSlimeSize() + tier;
        }

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setAttribute(baseHealth);

        // Heal to new max HP since applyEntityAttributes is called at construction time
        this.setHealth((float) (baseHealth * baseHealth));
    }
}
