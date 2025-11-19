package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityBlaze;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityBlaze.class)
public abstract class EntityBlazeMixin extends EntityLivingBase {
    public EntityBlazeMixin(World par1World) {
        super(par1World);
    }

    @Override
    public int getMaxHealth() {
        return this.worldObj == null ? 20 : MEAUtils.getGameProgressMobsLevel(this.worldObj) > 1 ? 24 : 20;
    }
}
