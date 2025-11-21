package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.mixin.access.EntityPigZombieAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPigZombie.class)
public abstract class EntityPigZombieMixin extends EntityZombie implements EntityPigZombieAccess {
    @SuppressWarnings("unused")
    private EntityPigZombieMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        float f = this.worldObj == null ? 1.5F : MEAUtils.getGameProgressMobsLevel(this.worldObj) > 1 ? 6.0F : 1.5F;

        EntityPlayer closestPlayer = this.worldObj == null ? null : this.worldObj.getClosestPlayerToEntity(this, f);

        if (closestPlayer != null) {
            this.becomeAngryAt(closestPlayer);
        }
    }
}
