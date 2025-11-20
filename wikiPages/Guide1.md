# **Compatible Entity Extensions Guide: Part 1 — Extending Vanilla Entities with Mixins: Understanding the Pattern**

**Audience:** Beginners just learning mixins and modding concepts.
**Goal:** Teach the *why* and *what* of safely extending vanilla classes, without overwhelming code.

---

## **Why Subclassing Vanilla Entities Isn’t Ideal**

In Minecraft modding, you might want to add new behavior or state to existing entities like `EntitySlime`. A common thought is to subclass the entity, but this approach often fails:

* Vanilla and other mods expect instances of the original class. Subclasses may break compatibility.
* Reflection or `@Accessor` hacks can be messy and error-prone.
* Hard to maintain as Minecraft updates.

**Solution:** Use Mixins to *inject* new fields and behavior into existing classes, while keeping the original class intact.

---

## **The General Pattern**

The pattern has four main components:

1. **Interface:** Defines the new API your extended entity will expose.

   * Contains methods only, no state.
   * Helps keep your mod organized.
   * Example: `getIsMagma()`, `setIsMagma(boolean)`.

2. **Mixin:**

   * Injects new fields into the target class.
   * Implements the interface methods.
   * Initializes state during construction.
   * Can include extra behavior methods.

3. **Helper Method:**

   * Provides a clean, centralized way to cast the entity to the extended interface.
   * Keeps code readable and safe.

4. **Optional: Interface Injection (via Loom):**

   * Makes the target class officially implement the interface at runtime.
   * Ensures casts always succeed without additional runtime checks.

---

## **Minimal Conceptual Example**

**Interface:**

```java
public interface EntitySlimeExtend {
    boolean mea$getIsMagma();
    void mea$setIsMagma(boolean value);
}
```

**Mixin (partial snippet):**

```java
@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin implements EntitySlimeExtend {
    @Unique private boolean isMagma;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(World world, CallbackInfo ci) {
        isMagma = false;
    }

    @Override
    public boolean mea$getIsMagma() { return isMagma; }
    @Override
    public void mea$setIsMagma(boolean value) { isMagma = value; }
}
```

**Helper:**

```java
public static EntitySlimeExtend asExtended(EntitySlime slime) {
    return (EntitySlimeExtend) slime;
}
```

---

### **Why This Pattern Works**

* **Safe:** No subclassing, no reflection hacks.
* **Compatible:** Vanilla and other mods continue to work.
* **Clean:** All new functionality is behind a simple interface.
* **Extensible:** Add fields or methods without touching vanilla code.
* **Reusable:** Works for any vanilla class or entity.
