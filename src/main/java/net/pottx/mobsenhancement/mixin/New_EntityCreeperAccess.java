package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityCreeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityCreeper.class)
public interface New_EntityCreeperAccess {
    @Accessor(value = "determinedToExplode", remap = false)
    void setIsDeterminedToExplode(boolean determinedToExplode);
}
