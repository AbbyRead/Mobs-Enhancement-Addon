Based on the code snippets provided so far, here is the feature list organized by confirmed and unconfirmed implementation. Specific implementation details (like exact values, conditions, and class names) have been added to the confirmed features.

***

## ✅ Confirmed Features with Implementation Details

These features have been confirmed via direct inspection of the Java mixin code.

### All Mobs (Confirmed)
* **Health Scaling:** Hostile mobs' Max Health increases at different game progress tiers: **Tier 1 (Nether Portal) and Tier 2+ (Wither Summoned)**.
* **Targeting Range:** Mobs' senses are strengthened, with notable target distances increased (e.g., **Zombies/Witches: 24.0F, Wither: 60.0D**).
* **Zombie AI Cleanup:** **Zombies** explicitly remove the default `EntityAIWatchClosest` task.

### Zombies (Confirmed)
* **Block Breaking:** Zombies are able to break certain types of blocks blocking the way to their target. **(Implemented with `EntityAIBreakBlock`)**
* **Health Scaling:** Base Max Health: **16.0 HP (T0), 20.0 HP (T1), 24.0 HP (T2+)**.
* **X-Ray Sight:** Zombies have a chance to sense targets through opaque blocks. **(Targeting logic checks `meap$getCanXray()`)**
* **Transformation:** When killed by the sun, the Zombie is instantly replaced by an **`EntitySkeleton`** at the same location, with **half its max health** and all **armor/held items copied**.
* **Targeting Expansion:** Zombies now target **`EntityVillager`** and **all passive `EntityCreature`** types.

### Skeletons (Confirmed)
* **Sun Immunity:** Skeletons no longer burn under sunlight. **(The check for catching fire is explicitly skipped)**
* **Wither Skeleton Spawning:** Skeletons have a **1/4 chance** to spawn as Wither Skeletons in the Overworld, below **Y=32**.
* **Accuracy Scaling:** Ranged accuracy is tied to game progress. Arrow deviation reduces from **8F (T0) to 2F (T3+)**.
* **Bow Durability:** Skeletons' bows have limited durability. If the bow breaks, the Skeleton is instantly replaced with a new one that has no bow.
* **Torch Breaking:** Skeletons try to break nearby burning torches. **(Implemented with `SkeletonBreakTorchBehavior`)**

### Witches (Confirmed)
* **Health Scaling:** Max Health: **24.0 HP (T0), 28.0 HP (T1+)**.
* **Targeting:** Witches target **`EntityVillager.class`**.
* **Evasion:** Witches try to flee from explosions. **(Implemented with `EntityAIFleeFromExplosion`)**

### Spiders (Confirmed)
* **Potion Effects:** Tier 1+ Spiders have a **1/16 chance** each to spawn with infinite **Speed, Strength, or Invisibility**.
* **Cobweb Effect:** Cobwebs make players hardly rotate their look direction. **(Player look rotation is slowed by a factor of 0.25F)**
* **Health Scaling:** Max Health: **16.0 HP (T0), 20.0 HP (T1+)**.

### Slimes (Confirmed)
* **Core/Merging:** Slimes smaller than size 4 have a **1/4 chance** to be designated as a "Core" Slime, with fields **`isMerging`** and **`mergeCooldownCounter`** added to support the merge mechanic.

### Silverfish (Confirmed)
* **Splitting:** When attacked, the Silverfish splits into two new ones, each with **half HP** of the mother and opposite momentum.
* **Health Scaling:** Max Health: **8.0 HP (T0), 12.0 HP (T1+)**.

### Zombie Pigmen (Confirmed)
* **Proximity Aggro:** Zombie Pigmen get invoked by nearby creatures. Aggro range is **1.5 blocks (T0/T1) or 6.0 blocks (T2+)**.

### Ghasts (Confirmed)
* **Projectile Immunity:** Arrows and throwable items cannot collide with Ghasts. **(Collision prevented for `EntityThrowable` (e.g., snowballs, eggs, pearls))**
* **Fireball Explosion:** Ghasts' fireballs cause an **additional small explosion (power 1)** on impact if game progress is **Tier 2+**.

### Withers (Confirmed)
* **Follow Range:** Withers follow their target at a larger distance. **(Follow range attribute increased to 60.0D)**
* **Minion Summon:** Withers sometimes summon Wither Skeletons when health is above half. **(1/400 chance on AI update when HP > 50%)**
* **Dash Attack:** Withers sometimes dash toward their target when health is below half. **(1/200 chance on AI update when HP < 50%)**

### Passive Mobs (Confirmed)
* **Villager Trading:** Villagers refuse to trade with players who have low reputation. **(Refuse if village reputation is < -5)**
* **Villager Evasion:** Villagers try to flee from explosions. **(Implemented with `EntityAIFleeFromExplosion`)**
* **Sheep Combat:** Sheep counterattack against zombies. **(Flee behavior removed and replaced with `AnimalCombatBehavior` against `EntityZombie.class`)**

***

## ❌ Remaining Unconfirmed Features

These features are listed in the document but the corresponding code logic has not been found yet.

### All Mobs (Unconfirmed)
* Mobs enjoy increased melee attack reach distance when holding a tool.
* Mobs cannot see entities behind them unless they are very close.
* Mobs can see through transparent blocks such as glass, leaves.
* Hostile mobs have a higher chance to spawn in groups after the first end portal is activated.

### Skeletons (Unconfirmed)
* Skeletons move faster.
* Skeletons have a chance to spawn with a melee weapon instead of a bow.
* Skeletons predict their target's position before ranged attacks.
* Skeletons try not to get too close to dangerous targets when holding a bow.
* Skeletons flee from dangerous targets when their health is low.
* Skeletons' base max health is decreased from 20 to 12.
* Skeletons target villagers.
* Skeletons have a chance to be able to sense targets through opaque blocks.
* Skeletons try to flee from explosions.

### Creepers (Unconfirmed)
* Creepers have a chance to spawn charged after the first nether portal is constructed.
* Creepers' base fuse time is decreased from 1.5 seconds to 1.25 second.
* Creepers' fuse time decrease when the first nether portal is constructed.
* Creepers' explosions spawn at the center of them, instead of the bottom.
* Creepers have a chance to instantly explode when interacted with shears, the chance is higher when charged.
* Creepers explode when damaged by explosions.
* Creepers don't immediately stop fusing on sight being blocked.
* Creepers have a chance to be able to sense targets through opaque blocks.

### Witches (Unconfirmed)
* Witches move faster.
* Witches predict their target's position before throwing potions.
* Witches try not to get too close to dangerous targets.
* Witches have a chance to be able to sense targets through opaque blocks.

### Silverfish (Unconfirmed)
* Infested stones generate in all biomes; the deeper, the more.

### Zombie Pigmen (Unconfirmed)
* Zombie pigmen are able to break certain types of blocks blocking the way to their attack target, determined by their held item.

### Magma Cubes (Unconfirmed)
* Small magma cubes melt into a puddle of lava on death.
* Magma cubes regenerate their health when touching lava.

### Blazes (Unconfirmed)
* Blazes' fireballs cause explosions after the first wither is summoned.

### Ghasts (Unconfirmed)
* Ghasts have a translucent look.
* Ghasts only take damage from fireballs and magic.

### Endermen (Unconfirmed)
* Endermen sometimes try to teleport their attack target when they cannot reach them.
* Players' gloom level rapidly increase when starring at an enderman.
* Players' gloom level rapidly increase when being too close to an enderman.

### Ender Dragons (Unconfirmed)
* When hit, instead of disappearing, ender crystals become dried, in this state they cannot heal the ender dragon.
* When hit, ender crystals avenge the attacker with a lightning bolt.
* Ender crystals try to charge nearby dried crystals, helping them restore vitality.
* Players' gloom level rapidly increase when being close to aner dragon.

### Spawners (Unconfirmed)
* Spawners curse nearby players with negative potion effects when broken.

### Passive Mobs (Unconfirmed)
* Pigs counterattack at high health.
* Some cows counterattack to protect weaker ones in the herd.
* Players' view is locked on first person when head crabbed by a squid.
* Animals have enhanced fleeing AI.
* Animals panic when pushed by a player.
* Players' reputation decrease if they attack villagers in a village.