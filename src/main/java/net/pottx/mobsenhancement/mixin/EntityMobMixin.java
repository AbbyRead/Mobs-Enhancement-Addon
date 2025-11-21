package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityMob;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.extend.EntityMobExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMob.class)
public abstract class EntityMobMixin extends EntityCreature implements EntityMobExtend {
    @Unique
    private static final int CAN_XRAY_DATA_WATCHER_ID = 31;

    @SuppressWarnings("unused")
    private EntityMobMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addXrayData(CallbackInfo ci) {
        dataWatcher.addObject(CAN_XRAY_DATA_WATCHER_ID, (byte)0);
        if (this.rand.nextInt(4) == 0) this.meap$setCanXray((byte)1);
    }

    @Unique
    public byte meap$getCanXray() {
        return this.dataWatcher.getWatchableObjectByte(CAN_XRAY_DATA_WATCHER_ID);
    }

    @Unique
    public void meap$setCanXray(byte canXray) {
        this.dataWatcher.updateObject(CAN_XRAY_DATA_WATCHER_ID, canXray);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setByte("CanXray", this.meap$getCanXray());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);

        this.meap$setCanXray(par1NBTTagCompound.getByte("CanXray"));
    }
}
