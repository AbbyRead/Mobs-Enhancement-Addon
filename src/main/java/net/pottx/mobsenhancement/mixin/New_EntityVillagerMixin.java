package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAIFleeFromExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityVillager.class)
public abstract class New_EntityVillagerMixin
        extends EntityAgeable
        implements IMerchant,
        INpc {
    public New_EntityVillagerMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/src/World;I)V",
            at = @At(value = "TAIL")
    )
    private void addFleeFromExplosionTask(CallbackInfo ci) {
        tasks.addTask(1, new EntityAIFleeFromExplosion(this, 0.35F, 4.0F));
        tasks.addTask(1, new EntityAIAvoidEntity(this, EntitySkeleton.class, 20.0F, 0.3F, 0.35F));
        tasks.addTask(1, new EntityAIAvoidEntity(this, EntityWitch.class, 20.0F, 0.3F, 0.35F));
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        if (this.villageObj != null && par1DamageSource.getEntity() instanceof EntityPlayer) {
            this.villageObj.setReputationForPlayer(((EntityPlayer)par1DamageSource.getEntity()).getCommandSenderName(), -1);
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }
}
