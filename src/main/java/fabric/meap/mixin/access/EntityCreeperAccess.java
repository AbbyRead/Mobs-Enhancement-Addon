package fabric.meap.mixin.access;

import net.minecraft.src.EntityCreeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityCreeper.class)
public interface EntityCreeperAccess {
    @Accessor("fuseTime")
    void setFuseTime(int fuseTime);

    @Accessor(value = "determinedToExplode", remap = false)
    void setIsDeterminedToExplode(boolean determinedToExplode);
}
