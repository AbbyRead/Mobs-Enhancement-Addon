package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAIFleeFromExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements IMerchant, INpc {
    @Shadow public Village villageObj;

    @SuppressWarnings("unused")
    private EntityVillagerMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "interact",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;displayGUIMerchant(Lnet/minecraft/src/IMerchant;Ljava/lang/String;)V"),
            cancellable = true
    )
    private void refuseToTradeWithVillain(EntityPlayer par1EntityPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (this.villageObj != null && this.villageObj.getReputationForPlayer(par1EntityPlayer.getCommandSenderName()) < -5) {
            cir.setReturnValue(false);
        }
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

}
