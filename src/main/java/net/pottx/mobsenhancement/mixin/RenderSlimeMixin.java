package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntitySlime;
import net.minecraft.src.RenderSlime;
import net.minecraft.src.ResourceLocation;
import net.pottx.mobsenhancement.extend.EntitySlimeExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSlime.class)
public abstract class RenderSlimeMixin {

	// Texture paths (your core slime ones)
	@Unique
	private static final ResourceLocation CORE_SLIME_1 =
			new ResourceLocation("meatextures/core_slime_1.png");
	@Unique
	private static final ResourceLocation CORE_SLIME_2 =
			new ResourceLocation("meatextures/core_slime_2.png");

	/**
	 * Inject into RenderSlime.getSlimeTextures(EntitySlime)
	 * and return a custom ResourceLocation for core slimes.
	 */
	@Inject(
			method = "getSlimeTextures(Lnet/minecraft/src/EntitySlime;)Lnet/minecraft/src/ResourceLocation;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void replaceSlimeTexture(EntitySlime slime, CallbackInfoReturnable<ResourceLocation> cir) {

		// Cast to your mixin so we can read getIsCore()
		EntitySlimeExtend extendedSlime = (EntitySlimeExtend) slime;

		// Skip magma cube / other variants
		if (!extendedSlime.meap$getIsMagma() && extendedSlime.meap$getIsCore() == (byte)1) {
			cir.setReturnValue(
					switch (slime.getSlimeSize()) {
						case 1 -> CORE_SLIME_1;
						case 2 -> CORE_SLIME_2;
						default -> cir.getReturnValue(); // keep existing value for other sizes
					}
			);
		}
		// Otherwise: let RenderSlime use normal texture
	}
}
