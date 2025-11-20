# **Extending Vanilla Entities with Mixins**

Mixin lets you add behavior, state, and APIs to existing vanilla classes without needing to subclass them.
This guide walks through a complete, practical pattern for extending `EntitySlime` by:

* injecting new fields
* exposing them through an interface
* initializing them during construction
* accessing them safely from other code
* optionally injecting the interface at runtime for cleaner semantics

This pattern generalizes to **any entity or vanilla class**.

---

# **1. Define an Interface for Your Added Fields**

The interface represents the new API you want every `EntitySlime` instance to have.
It contains *only methods*, no state — the state will be injected into the target class by the mixin.

```java
package net.pottx.mobsenhancement.extend;

/**
 * API for additional slime properties injected with a Mixin.
 *
 * Prefixing method names (e.g., mea$) avoids accidental collisions
 * with vanilla or other mods.
 */
public interface EntitySlimeExtend {

    boolean mea$getIsMagma();
    void mea$setIsMagma(boolean value);

    byte mea$getIsCore();
    void mea$setIsCore(byte id);

    boolean mea$getIsMerging();
    void mea$setIsMerging(boolean value);
}
```

**Why this step matters:**
Defining a public API up front keeps your mod organized and prevents you from relying on `@Accessor` or reflection for fields you own.

---

# **2. Inject Fields and Implement the Interface via a Mixin**

The mixin adds new fields, initializes them, and makes them part of the entity’s behavior.

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

    // ---------------------------------------------------------
    // Injected fields (stored in the real EntitySlime instances)
    // ---------------------------------------------------------
    @Unique private boolean isMagma;
    @Unique private boolean isMerging;
    @Unique private int mergeCooldownCounter;

    @Unique private static final int IS_CORE_DATA_WATCHER_ID = 25;

    // Stub constructor to satisfy the superclass
    private EntitySlimeMixin(World world) {
        super(world);
    }

    // ---------------------------------------------------------
    // Shadow vanilla methods used internally
    // ---------------------------------------------------------
    @Shadow protected abstract void setSlimeSize(int size);
    @Shadow protected abstract EntitySlime createInstance();
    @Shadow public abstract int getSlimeSize();

    // ---------------------------------------------------------
    // Interface implementation
    // ---------------------------------------------------------
    @Override
    public boolean mea$getIsMagma() { return isMagma; }

    @Override
    public void mea$setIsMagma(boolean value) { isMagma = value; }

    @Override
    public boolean mea$getIsMerging() { return isMerging; }

    @Override
    public void mea$setIsMerging(boolean value) { isMerging = value; }

    @Override
    public byte mea$getIsCore() {
        return dataWatcher.getWatchableObjectByte(IS_CORE_DATA_WATCHER_ID);
    }

    @Override
    public void mea$setIsCore(byte value) {
        dataWatcher.updateObject(IS_CORE_DATA_WATCHER_ID, value);
    }

    // ---------------------------------------------------------
    // Constructor inject: initialize new fields
    // ---------------------------------------------------------
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(World world, CallbackInfo ci) {
        isMagma = false;
        isMerging = false;
        mergeCooldownCounter = 40;

        dataWatcher.addObject(IS_CORE_DATA_WATCHER_ID, (byte) 0);
    }

    // Example method using the injected fields
    @Unique
    public void exampleMergeLogic() {
        if (isMagma) return;
        if (mea$getIsCore() != 1) return;

        mergeCooldownCounter--;
        if (mergeCooldownCounter <= 0) {
            isMerging = false;
            mergeCooldownCounter = 40;
        }
    }
}
```

**What this gives you:**

* Your fields now physically exist in the real `EntitySlime` objects.
* Every slime now implements your interface.
* No subclassing, no coremodding, no reflection.

---

# **3. Accessing Your Extended Data**

Java doesn’t know that `EntitySlime` has your interface, so one cast is needed.
A helper method keeps that cast safe, consistent, and readable.

```java
public static EntitySlimeExtend extend(EntitySlime slime) {
    return (EntitySlimeExtend) slime;
}

// Usage:
EntitySlime slime = ...;

EntitySlimeExtend ext = extend(slime);
ext.mea$setIsMagma(true);

if (ext.mea$getIsCore() == 1) {
    ext.mea$setIsMerging(false);
}
```

This cast is always safe at runtime, because the mixin and (optionally) interface injection guarantee the interface exists.

---

# **4. Optional: Have Loom Inject the Interface Automatically**

Fabric Loom can append your interface to the target class so it *officially* implements it at runtime.

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

**Why this is useful:**

* All `EntitySlime` instances now *genuinely* implement your interface.
* The `(EntitySlimeExtend)` cast will always succeed.
* Keeps mod-wide behavior consistent and predictable.

---

# **Why This Pattern Works Well**

* **Compatible**: No subclassing means you don’t break vanilla expectations.
* **Safe**: Interface implementation is guaranteed at runtime.
* **Clean**: New code lives behind a simple API (`EntitySlimeExtend`).
* **Extensible**: Add new fields or methods without touching the original class.
* **Reusable**: The same pattern applies to any entity or vanilla class.

This is one of the most reliable and future-proof ways to extend vanilla entities using Mixins.
