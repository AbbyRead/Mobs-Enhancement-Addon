# Extending `EntitySlime` Without Subclassing

This guide walks through the exact pattern I've used to add fields and behavior to `EntitySlime` using Mixins. The approach injects new fields, exposes them through an interface, initializes them during construction, and provides a helper method for clean access across the mod.

---

## 1. Interface (`EntitySlimeExtend.java`)

`EntitySlimeExtend` defines the accessor methods used to interact with the injected fields. Methods use the `meap$` prefix to avoid potential collisions with vanilla code or other mods. The interface contains no state; it only specifies the methods implemented by the mixin.

```java
package net.pottx.mobsenhancement.extension;

// Methods are prefixed with `meap$` to avoid naming conflicts.
public interface EntitySlimeExtend {

	// Magma flag
	boolean meap$getIsMagma();

	void meap$setIsMagma(boolean value);

	// Core flag stored in DataWatcher
	byte meap$getIsCore();

	void meap$setIsCore(byte id);

	// Merging state
	boolean meap$getIsMerging();

	void meap$setIsMerging(boolean value);
}
```

---

## 2. Mixin (`EntitySlimeMixin.java`)

The mixin injects new fields into `EntitySlime`, exposes them through the interface methods, and initializes them when the vanilla constructor finishes. Fields added to the target class use `@Unique` to ensure no name collisions occur.

```java
package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.extension.EntitySlimeExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extends EntitySlime with addon-specific behavior and fields.
 */
@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin extends EntityLiving implements EntitySlimeExtend {

	// Fields injected directly into EntitySlime
	@Unique
	private boolean isMagma;
	@Unique
	private boolean isMerging;
	@Unique
	private int mergeCooldownCounter;

	// DataWatcher slot chosen to avoid vanilla collisions
	@Unique
	private static final int IS_CORE_DATA_WATCHER_ID = 25;

	// Required stub constructor
	private EntitySlimeMixin(World world) {
		super(world);
	}

	// Shadow required vanilla methods
	@Shadow
	protected abstract void setSlimeSize(int size);

	@Shadow
	protected abstract EntitySlime createInstance();

	@Shadow
	public abstract int getSlimeSize();

	// Interface implementations
	@Override
	public boolean meap$getIsMagma() {
		return isMagma;
	}

	@Override
	public void meap$setIsMagma(boolean value) {
		this.isMagma = value;
	}

	@Override
	public boolean meap$getIsMerging() {
		return isMerging;
	}

	@Override
	public void meap$setIsMerging(boolean value) {
		this.isMerging = value;
	}

	@Override
	public byte meap$getIsCore() {
		return this.dataWatcher.getWatchableObjectByte(IS_CORE_DATA_WATCHER_ID);
	}

	@Override
	public void meap$setIsCore(byte value) {
		this.dataWatcher.updateObject(IS_CORE_DATA_WATCHER_ID, value);
	}

	// Initialization of injected state
	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(World world, CallbackInfo ci) {
		this.isMagma = false;
		this.isMerging = false;
		this.mergeCooldownCounter = 40;
		this.dataWatcher.addObject(IS_CORE_DATA_WATCHER_ID, (byte) 0);
	}

	// Example method using injected fields
	@Unique
	public void exampleMergeLogic() {
		if (isMagma) return;
		if (meap$getIsCore() != 1) return;

		mergeCooldownCounter--;
		if (mergeCooldownCounter <= 0) {
			this.isMerging = false;
			mergeCooldownCounter = 40;
		}
	}
}
```

---

## 3. Accessing the Extended Fields in Your Mod

Because Java does not know at compile time that `EntitySlime` implements `EntitySlimeExtend`, a cast is required. A single helper method keeps this cast centralized, making the rest of your code cleaner.

```java
// Centralize the cast in a single helper method.
public static EntitySlimeExtend asExtended(EntitySlime slime) {
    return (EntitySlimeExtend) slime;
}

// Usage
EntitySlime slime = ...; // instance from the game
EntitySlimeExtend ext = asExtended(slime);

ext.meap$setIsMagma(true);
ext.meap$setIsMerging(true);

if (ext.meap$getIsCore() == 1) {
    ext.meap$setIsMerging(false);
}
```

---

## 4. Interface Injection in `fabric.mod.json`

Loom can inject the interface into `EntitySlime` at runtime, ensuring the cast used above is always valid. The compiler still requires the cast, but the helper method guarantees consistent access throughout the codebase.

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

With this configuration, every `EntitySlime` instance created by the game now fully implements `EntitySlimeExtend`, and your injected fields and methods are available everywhere through the helper.
