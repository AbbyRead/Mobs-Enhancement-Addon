package fabric.meap.mixin.entity.hostile;

import fabric.meap.mixin.access.EntitySlimeAccess;
import net.minecraft.src.*;
import btw.community.abbyread.meap.extend.EntitySlimeExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin extends EntityLiving implements EntitySlimeExtend, EntitySlimeAccess {

    // -------------------------------------------------------------------
    // Unique Fields
    // -------------------------------------------------------------------
    @Unique private boolean isMagma;
    @Unique private int mergeCooldownCounter;
    @Unique private boolean isMerging;
    @Unique private static final int IS_CORE_DATA_WATCHER_ID = 25;
    @Unique private boolean initializedFromNBT = false;

    protected EntitySlimeMixin(World world) {
        super(world);
    }

    // -------------------------------------------------------------------
    // Shadows
    // -------------------------------------------------------------------
    @Shadow protected abstract EntitySlime createInstance();
    @Shadow public abstract int getSlimeSize();

    // -------------------------------------------------------------------
    // Interface Implementation
    // -------------------------------------------------------------------
    @Override public boolean meap$getInitializedFromNBT() { return this.initializedFromNBT; }
    @Override public void meap$setInitializedFromNBT(boolean value) { this.initializedFromNBT = value; }

    @Override public boolean meap$getIsMagma() { return this.isMagma; }
    @Override public void meap$setIsMagma(boolean value) { this.isMagma = value; }

    @Override public boolean meap$getIsMerging() { return this.isMerging; }
    @Override public void meap$setIsMerging(boolean value) { this.isMerging = value; }

    @Override public byte meap$getIsCore() {
        return this.dataWatcher.getWatchableObjectByte(IS_CORE_DATA_WATCHER_ID);
    }
    @Override public void meap$setIsCore(byte value) {
        this.dataWatcher.updateObject(IS_CORE_DATA_WATCHER_ID, value);
    }

    // -------------------------------------------------------------------
    // Constructor Defaults (Safe)
    // -------------------------------------------------------------------
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(World world, CallbackInfo ci) {
        this.isMagma = false;
        this.mergeCooldownCounter = 40;
        this.isMerging = false;
    }

    // -------------------------------------------------------------------
    // DataWatcher Init (Safe)
    // -------------------------------------------------------------------
    @Inject(method = "entityInit", at = @At("TAIL"))
    private void onEntityInit(CallbackInfo ci) {
        this.dataWatcher.addObject(IS_CORE_DATA_WATCHER_ID, (byte)0);
    }

    // -------------------------------------------------------------------
    // NBT Save/Load
    // -------------------------------------------------------------------
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setByte("IsCore", this.meap$getIsCore());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.meap$setIsCore(tag.getByte("IsCore"));
        this.meap$setInitializedFromNBT(true); // Prevent core assignment on loaded slimes
    }

    // -------------------------------------------------------------------
    // Merge Logic
    // -------------------------------------------------------------------
    @SuppressWarnings("DiscouragedShift")
    @Inject(method = "updateEntityActionState()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntitySlime;faceEntity(Lnet/minecraft/src/Entity;FF)V",
            shift = At.Shift.BY, by = 2
    ))
    private void meap$mergeLogic(CallbackInfo ci) {
        if (this.meap$getIsMagma()) return;

        // Core merging
        if (this.meap$getIsCore() == 1) {
            mergeCooldownCounter--;
            if (mergeCooldownCounter <= 0) {
                @SuppressWarnings("unchecked")
                List<EntitySlime> nearby = this.worldObj.getEntitiesWithinAABB(EntitySlime.class, this.boundingBox.expand(12,6,12));
                nearby.removeIf(s -> ((EntitySlimeExtend)s).meap$getIsCore() != 0
                        || s.getSlimeSize() != this.getSlimeSize()
                        || !s.isEntityAlive());

                if (nearby.size() >= 2) {
                    if (this.isMerging) {
                        EntitySlime target = nearby.get(0);
                        this.faceEntity(target, 10, 20);

                        double range = (this.getSlimeSize() == 1 ? 0.75 : 1.0);
                        @SuppressWarnings("unchecked")
                        List<EntitySlime> veryClose = this.worldObj.getEntitiesWithinAABB(EntitySlime.class, this.boundingBox.expand(range, range, range));
                        veryClose.removeIf(s -> ((EntitySlimeExtend)s).meap$getIsCore() != 0
                                || s.getSlimeSize() != this.getSlimeSize()
                                || !s.isEntityAlive());

                        if (veryClose.size() >= 2) {
                            this.worldObj.playAuxSFX(1005,
                                    MathHelper.floor_double(this.posX),
                                    MathHelper.floor_double(this.posY),
                                    MathHelper.floor_double(this.posZ), 0);

                            ((EntitySlimeAccess)this).invokeSetSlimeSize(this.getSlimeSize() * 2);
                            if (this.getSlimeSize() == 4)
                                this.meap$setIsCore((byte)0);

                            for (int i=0; i<2; i++)
                                veryClose.get(i).setDead();

                            this.isMerging = false;
                            this.mergeCooldownCounter = 40;
                            nearby.forEach(s -> ((EntitySlimeExtend)s).meap$setIsMerging(false));
                        }
                    } else {
                        this.isMerging = true;
                        nearby.forEach(s -> ((EntitySlimeExtend)s).meap$setIsMerging(true));
                    }
                } else if (this.isMerging) {
                    this.isMerging = false;
                    this.mergeCooldownCounter = 40;
                    nearby.forEach(s -> ((EntitySlimeExtend)s).meap$setIsMerging(false));
                }
            }
        }
        // Non-core: follow core
        else {
            @SuppressWarnings("unchecked")
            List<EntitySlime> nearby = this.worldObj.getEntitiesWithinAABB(EntitySlime.class, this.boundingBox.expand(12,6,12));
            EntitySlime core = nearby.stream()
                    .filter(s -> ((EntitySlimeExtend)s).meap$getIsCore() != 0 && s.isEntityAlive())
                    .findFirst().orElse(null);

            if (this.isMerging) {
                if (core != null) this.faceEntity(core, 10, 20);
                else this.isMerging = false;
            }
        }
    }

    // -------------------------------------------------------------------
    // XP on small slimes
    // -------------------------------------------------------------------
    @Inject(method = "setSlimeSize", at = @At("TAIL"))
    private void meap$xpOnSmall(int size, CallbackInfo ci) {
        if (!isMagma) this.experienceValue = (size == 1 ? 2 : 0);
    }

    // -------------------------------------------------------------------
    // Helper for splitting
    // -------------------------------------------------------------------
    @Unique
    private EntitySlime meap$simpleCreateInstance() {
        EntitySlime inst = this.createInstance();
        ((EntitySlimeExtend)inst).meap$setIsCore((byte)0);
        return inst;
    }

    // -------------------------------------------------------------------
    // Slime splitting (core inheritance)
    // -------------------------------------------------------------------
    @Override
    public void setDead() {
        int size = this.getSlimeSize();
        if (!this.worldObj.isRemote && size > 1 && this.getHealth() <= 0) {
            int count = 2 + this.rand.nextInt(3);
            boolean coreNeeded = (size == 4) || this.meap$getIsCore() == 1;

            for (int i = 0; i < count; i++) {
                float dx = ((i % 2) - 0.5F) * size / 40F;
                float dz = (((float) i / 2) - 0.5F) * size / 40F;

                EntitySlime child = this.meap$simpleCreateInstance();
                if (coreNeeded) {
                    ((EntitySlimeExtend)child).meap$setIsCore((byte)1);
                    coreNeeded = false;
                }

                ((EntitySlimeAccess)child).invokeSetSlimeSize(size / 2);
                child.setLocationAndAngles(this.posX + dx, this.posY + 0.5, this.posZ + dz,
                        this.rand.nextFloat() * 360F, 0);

                this.worldObj.spawnEntityInWorld(child);
            }
        }
        this.isDead = true;
    }
}
