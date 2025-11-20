package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.extend.EntitySilverfishExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySilverfish.class)
public abstract class EntitySilverfishMixin extends EntityMob implements EntitySilverfishExtend {
    private EntitySilverfishMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "applyEntityAttributes", at = @At("RETURN"), cancellable = true)
    private void modifyMaxHealth(CallbackInfo ci) {
        // Default init to normal intitialization value
        double baseHealth = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();

        if (this.worldObj != null) {
            int tier = MEAUtils.getGameProgressMobsLevel(this.worldObj);
            baseHealth = tier > 0 ? 12 : 8;
        }

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setAttribute(baseHealth);

        // Heal to new max HP since applyEntityAttributes is called at construction time
        this.setHealth((float) baseHealth);
        ci.cancel();
    }

    @Unique
    public void mea$split() {
        double vx = this.motionZ * (0.25 + this.rand.nextDouble() * 0.5);
        double vz = 0 - this.motionX * (0.25 + this.rand.nextDouble() * 0.5);
        int hp = MathHelper.ceiling_double_int(this.getHealth() * 0.5);

        if (this.worldObj.isRemote) return;

        EntitySilverfish child1 = new EntitySilverfish(this.worldObj);
        child1.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        child1.setVelocity(vx, 0, vz);
        child1.setHealth(hp);
        child1.spawnerInitCreature();

        EntitySilverfish child2 = new EntitySilverfish(this.worldObj);
        child2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        child2.setVelocity(-vx, 0, -vz);
        child2.setHealth(hp);
        child2.spawnerInitCreature();

        this.worldObj.spawnEntityInWorld(child1);
        this.worldObj.spawnEntityInWorld(child2);

        this.setDead();

    }

}
