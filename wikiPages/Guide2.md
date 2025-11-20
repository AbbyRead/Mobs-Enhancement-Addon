# **Compatible Entity Extensions Guide: Part 2 — Practical Example: Extending EntitySlime**

**Audience:** Beginners ready to see a full working implementation.
**Goal:** Show the pattern applied to a real entity (`EntitySlime`) with full code and explanation.

---

## **1. Interface (`EntitySlimeExtend.java`)**

Defines the methods used to access the injected fields. Methods are prefixed with `mea$` to avoid naming conflicts.

```java
package net.pottx.mobsenhancement.extend;

public interface EntitySlimeExtend {
    boolean mea$getIsMagma();
    void mea$setIsMagma(boolean value);

    byte mea$getIsCore();
    void mea$setIsCore(byte id);

    boolean mea$getIsMerging();
    void mea$setIsMerging(boolean value);
}
```

---

## **2. Mixin (`EntitySlimeMixin.java`)**

Injects new fields into `EntitySlime`, implements the interface, and initializes the state in the constructor.

```java
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

    @Unique private boolean isMagma;
    @Unique private boolean isMerging;
    @Unique private int mergeCooldownCounter;

    @Unique private static final int IS_CORE_DATA_WATCHER_ID = 25;

    private EntitySlimeMixin(World world) { super(world); }

    @Shadow protected abstract void setSlimeSize(int size);
    @Shadow protected abstract EntitySlime createInstance();
    @Shadow public abstract int getSlimeSize();

    @Override
    public boolean mea$getIsMagma() { return isMagma; }
    @Override
    public void mea$setIsMagma(boolean value) { this.isMagma = value; }

    @Override
    public boolean mea$getIsMerging() { return isMerging; }
    @Override
    public void mea$setIsMerging(boolean value) { this.isMerging = value; }

    @Override
    public byte mea$getIsCore() { return this.dataWatcher.getWatchableObjectByte(IS_CORE_DATA_WATCHER_ID); }
    @Override
    public void mea$setIsCore(byte value) { this.dataWatcher.updateObject(IS_CORE_DATA_WATCHER_ID, value); }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(World world, CallbackInfo ci) {
        this.isMagma = false;
        this.isMerging = false;
        this.mergeCooldownCounter = 40;
        this.dataWatcher.addObject(IS_CORE_DATA_WATCHER_ID, (byte)0);
    }

    @Unique
    public void exampleMergeLogic() {
        if (isMagma) return;
        if (mea$getIsCore() != 1) return;

        mergeCooldownCounter--;
        if (mergeCooldownCounter <= 0) {
            this.isMerging = false;
            mergeCooldownCounter = 40;
        }
    }
}
```

---

## **3. Helper Method**

A central place to cast an `EntitySlime` to the extended interface:

```java
public static EntitySlimeExtend asExtended(EntitySlime slime) {
    return (EntitySlimeExtend) slime;
}

// Usage
EntitySlime slime = ...;
EntitySlimeExtend ext = asExtended(slime);

ext.mea$setIsMagma(true);
ext.mea$setIsMerging(true);
```

---

## **4. Optional: Interface Injection in `fabric.mod.json`**

```json
"mixins": [
  "mobsenhancement.mixins.json"
],
"custom": {
  "loom:injected_interfaces": {
    "net/minecraft/src/EntitySlime": [
      "net/pottx/mobsenhancement/extend/EntitySlimeExtend"
    ]
  }
}
```

* Ensures all `EntitySlime` instances genuinely implement `EntitySlimeExtend` at runtime.
* Makes the helper cast always safe.

---

## ✅ **Why This Works**

* **Compatible:** Vanilla code and other mods continue to operate normally.
* **Safe:** No subclassing, reflection, or coremodding required.
* **Extensible:** You can add new fields or methods anytime.
* **Reusable:** The same pattern can apply to any entity or vanilla class.
