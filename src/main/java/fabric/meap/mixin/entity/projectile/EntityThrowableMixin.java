package fabric.meap.mixin.entity.projectile;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityThrowable;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityThrowable.class)
public abstract class EntityThrowableMixin extends Entity {
    @SuppressWarnings("unused")
    private EntityThrowableMixin(World par1World) {
        super(par1World);
    }

    @Redirect(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;canBeCollidedWith()Z")
    )
    private boolean notCollideWithGhasts(Entity var10) {
        return var10.canBeCollidedWith() && !(var10 instanceof EntityGhast);
    }
}
