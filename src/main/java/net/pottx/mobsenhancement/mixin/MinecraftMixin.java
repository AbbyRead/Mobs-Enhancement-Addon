package net.pottx.mobsenhancement.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public EntityClientPlayerMP thePlayer;

    @Shadow public GameSettings gameSettings;

    @Inject(
            method = "runGameLoop",
            at = @At(value = "TAIL")
    )
    private void lockThirdPersonViewOnHeadCrab(CallbackInfo ci) {
        if (this.thePlayer != null && this.thePlayer.hasHeadCrabbedSquid()) this.gameSettings.thirdPersonView = 0;
    }
}
