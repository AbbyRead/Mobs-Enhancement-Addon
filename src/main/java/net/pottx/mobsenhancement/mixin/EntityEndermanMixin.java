package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityEnderman.class)
public class EntityEndermanMixin extends EntityMob {
    @Shadow
    private int teleportDelay;

    private EntityEndermanMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderman;entityMobOnLivingUpdate()V")
    )
    private void updateEnemyTeleport(CallbackInfo ci)
    {
        if (!this.worldObj.isRemote && this.isEntityAlive() && this.entityToAttack != null) {
            if (entityToAttack.getDistanceSqToEntity(this) > 6.0D && entityToAttack.getDistanceSqToEntity(this) < 64D) {
                if (teleportDelay++ >= 120 && this.teleportEnemy()) {
                    this.teleportDelay = 0;
                }
            } else if (entityToAttack.getDistanceSqToEntity(this) < 256D) {
                this.teleportDelay = 0;
            }
        }
    }

    @Unique
    protected boolean teleportEnemy() {
        if (this.entityToAttack != null) {
            Entity target = this.entityToAttack;
            Vec3 vec = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX - target.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - target.posY + (double)target.getEyeHeight(), this.posZ - target.posZ);
            vec = vec.normalize();
            double x0 = target.posX;
            double y0 = target.posY;
            double z0 = target.posZ;
            target.posX = x0 + (this.rand.nextDouble() - 0.5D) * 8.0D + vec.xCoord * 0.8D;
            target.posY = y0 + (double)(this.rand.nextInt(16) - 8) + vec.yCoord * 0.8D;
            target.posZ = z0 + (this.rand.nextDouble() - 0.5D) * 8.0D + vec.zCoord * 0.8D;
            int xb = MathHelper.floor_double(target.posX);
            int yb = MathHelper.floor_double(target.posY);
            int zb = MathHelper.floor_double(target.posZ);
            int blockId;

            if (this.worldObj.blockExists(xb, yb, zb)) {
                boolean canTeleport = false;

                while (!canTeleport && yb > 0) {
                    blockId = this.worldObj.getBlockId(xb, yb, zb);
                    if (blockId != 0 && Block.blocksList[blockId].blockMaterial.blocksMovement()) {
                        canTeleport = true;
                        for (int i = 1; i <= MathHelper.ceiling_float_int(target.height); i++) {
                             blockId = this.worldObj.getBlockId(xb, yb + i, zb);
                             if (blockId != 0 && Block.blocksList[blockId].blockMaterial.blocksMovement()) {
                                 canTeleport = false;
                                 target.posY -= MathHelper.ceiling_float_int(target.height) + 1;
                                 yb -= MathHelper.ceiling_float_int(target.height) + 1;
                                 break;
                             }
                        }
                        ++target.posY;
                    }
                    else {
                        --target.posY;
                        --yb;
                    }
                }

                if (canTeleport) {
                    if (target instanceof EntityPlayer) {
                        ((EntityPlayer) target).setPositionAndUpdate(target.posX, target.posY, target.posZ);
                    } else {
                        target.setPosition(target.posX, target.posY, target.posZ);
                    }

                    short var30 = 128;
                    for (blockId = 0; blockId < var30; ++blockId) {
                        double var19 = (double)blockId / ((double)var30 - 1.0D);
                        float var21 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                        float var22 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                        float var23 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                        double var24 = x0 + (target.posX - x0) * var19 + (this.rand.nextDouble() - 0.5D) * (double)target.width * 2.0D;
                        double var26 = y0 + (target.posY - y0) * var19 + this.rand.nextDouble() * (double)target.height;
                        double var28 = z0 + (target.posZ - z0) * var19 + (this.rand.nextDouble() - 0.5D) * (double)target.width * 2.0D;
                        this.worldObj.spawnParticle("portal", var24, var26, var28, var21, var22, var23);
                    }
                    this.worldObj.playSoundEffect(x0, y0, z0, "mob.endermen.portal", 1.0F, 1.0F);
                    target.playSound("mob.endermen.portal", 1.0F, 1.0F);

                    return true;
                } else {
                    target.setPosition(x0, y0, z0);
                }
            }
        }
        return false;
    }
}
