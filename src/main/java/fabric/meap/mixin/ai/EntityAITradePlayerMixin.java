package fabric.meap.mixin.ai;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAITradePlayer.class)
public abstract class EntityAITradePlayerMixin extends EntityAIBase {
    @Shadow private EntityVillager villager;

    @Inject(
            method = "shouldExecute",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void executeIfAvoidingVillain(CallbackInfoReturnable<Boolean> cir) {
        EntityPlayer customer = this.villager.getCustomer();
        if (customer != null && this.villager.villageObj != null && villager.villageObj.getReputationForPlayer(customer.getCommandSenderName()) < -5) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method = "startExecuting",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PathNavigate;clearPathEntity()V")
    )
    private void avoidVillainCustomer(PathNavigate pathNavigate) {
        EntityPlayer customer = this.villager.getCustomer();
        if (this.villager.villageObj != null && villager.villageObj.getReputationForPlayer(customer.getCommandSenderName()) < -5) {
            Vec3 destination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.villager, 4, 2, this.villager.worldObj.getWorldVec3Pool().getVecFromPool(customer.posX, customer.posY, customer.posZ));
            this.villager.getNavigator().tryMoveToXYZ(destination.xCoord, destination.yCoord, destination.zCoord, 0.35F);
        } else {
            this.villager.getNavigator().clearPathEntity();
        }
    }
}
