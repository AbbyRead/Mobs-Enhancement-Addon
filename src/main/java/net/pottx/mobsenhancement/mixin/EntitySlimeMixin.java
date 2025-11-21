package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.extend.EntitySlimeExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin extends EntityLiving implements EntitySlimeExtend {

    // ------------------------------
    // Injected fields
    // ------------------------------
    @Unique private boolean isMagma;
    @Unique private int mergeCooldownCounter;
    @Unique private boolean isMerging;

    @Unique private static final int IS_CORE_DATA_WATCHER_ID = 25;

    @SuppressWarnings("unused")
    private EntitySlimeMixin(World world) {
        super(world);
    }

    // ------------------------------
    // Shadow existing methods
    // ------------------------------
    @SuppressWarnings("unused")
    @Shadow protected abstract void setSlimeSize(int size);
    @SuppressWarnings("unused")
    @Shadow protected abstract EntitySlime createInstance();
    @Shadow public abstract int getSlimeSize();

    // ------------------------------
    // Interface implementations
    // ------------------------------

    @Override
    public boolean meap$getIsMagma() {
        return this.isMagma;
    }

    @Override
    public void meap$setIsMagma(boolean value) {
        this.isMagma = value;
    }

    @Override
    public boolean meap$getIsMerging() { return this.isMerging; }

    @Override
    public void meap$setIsMerging(boolean value) { this.isMerging = value; }

    @Override
    public byte meap$getIsCore() {
        return this.dataWatcher.getWatchableObjectByte(IS_CORE_DATA_WATCHER_ID);
    }

    @Override
    public void meap$setIsCore(byte value) {
        this.dataWatcher.updateObject(IS_CORE_DATA_WATCHER_ID, value);
    }

    // ------------------------------
    // Example: initialization
    // ------------------------------
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(World world, CallbackInfo ci) {
        this.isMagma = false;
        this.mergeCooldownCounter = 40;
        this.dataWatcher.addObject(IS_CORE_DATA_WATCHER_ID, (byte)0);

        if (this.getSlimeSize() < 4 && this.rand.nextInt(4) == 0) {
            this.meap$setIsCore((byte)1);
        }
    }

    // ------------------------------
    // Example: merge logic using Mixin fields
    // ------------------------------
    @SuppressWarnings("unused")
    @Unique
    private void doMergeLogic() {
        if (isMagma) return;
        if (this.meap$getIsCore() != (byte)1) return;

        mergeCooldownCounter--;
        if (mergeCooldownCounter <= 0) {
            // ... merging logic here
            this.isMerging = false; // reset
            mergeCooldownCounter = 40;
        }
    }
}
