This feature list has been updated with the specific implementation details (values, triggers, class names) that have been confirmed from the code snippets.

**Bold text** indicates the specific implementation detail confirmed in the code.

## Features

### All Mobs

- Mobs enjoy increased melee attack reach distance when holding a tool. (`TODO: Confirm Reimplementation`)
- Mobs' senses are strengthened, with further target distance (e.g., **Zombies/Witches: 24.0F**, **Wither: 60.0D**) and more points to check, instead of only doing an eye-to-eye check.
- Mobs cannot see entities behind them unless they are very close. (`TODO: Confirm Reimplementation`)
- Mobs can see through transparent blocks such as glass, leaves. (`TODO: Confirm Reimplementation`, implied by x-ray flag)
- Mobs no longer try to stare at the closest player. (`TODO: Confirm Reimplementation`, except for Zombies which explicitly remove `EntityAIWatchClosest`)
- Hostile mobs' max health increase when the first nether portal is constructed. **(Triggered by `MEAUtils.getGameProgressMobsLevel` Tier 1)**
- Nether and undead mobs' max health increase when the first wither is summoned. **(Triggered by `MEAUtils.getGameProgressMobsLevel` Tier 2+)**
- Hostile mobs have a higher chance to spawn in groups after the first end portal is activated. (`TODO: Confirm Reimplementation`)

### Zombies

- Zombies are able to break certain types of blocks blocking the way to their attack target, determined by their held item. **(Implemented with `EntityAIBreakBlock`)**
- Zombies' base max health is decreased from 20 to 16. **(Scales: 16.0 HP (T0), 20.0 HP (T1), 24.0 HP (T2+))**
- Zombies have a chance to be able to sense targets through opaque blocks. **(Targeting logic checks `meap$getCanXray()`)**
- Zombies transform into a skeleton of half its max health when die of sunlight. **(Skeleton has `maxHealth / 2.0F` and copies all armor/held items)**

### Skeletons

- Skeletons move faster. (`TODO: Confirm Reimplementation` - `applyEntityAttributes` not provided)
- Skeletons no longer burn under sunlight. **(The check for catching fire is skipped)**
- Skeletons have a chance to spawn with a melee weapon instead of a bow. (`TODO: Confirm Reimplementation`)
- Skeletons have a chance to spawn as a wither skeleton in the overworld at a low Y level after the first nether portal is constructed. **(1/4 chance below Y=32 in the Overworld)**
- Skeletons predict their target's position before ranged attacks. (`TODO: Confirm Reimplementation`)
- Skeletons' accuracy increases when the first nether portal is constructed, and when the first wither is summoned. **(Arrow deviation reduces from 8F (T0) to 2F (T3+))**
- Skeletons' bows have limited durability, which increases when the first nether portal is constructed, and when the first wither is summoned. **(If bow breaks, a new Skeleton is spawned without a bow)**
- Skeletons try not to get too close to dangerous targets when holding a bow. (`TODO: Confirm Reimplementation`)
- Skeletons flee from dangerous targets when their health is low. (`TODO: Confirm Reimplementation`)
- Skeletons' base max health is decreased from 20 to 12. (`TODO: Confirm Reimplementation` - `applyEntityAttributes` not provided)
- Skeletons target villagers. (`TODO: Confirm Reimplementation`)
- Skeletons try to break nearby burning torches. **(Implemented with `SkeletonBreakTorchBehavior`)**
- Skeletons have a chance to be able to sense targets through opaque blocks. (`TODO: Confirm Reimplementation`)
- Skeletons try to flee from explosions. (`TODO: Confirm Reimplementation`)

### Creepers

- Creepers have a chance to spawn charged after the first nether portal is constructed. (`TODO: Confirm Reimplementation`)
- Creepers' base fuse time is decreased from 1.5 seconds to 1.25 second. (`TODO: Confirm Reimplementation`)
- Creepers' fuse time decrease when the first nether portal is constructed. (`TODO: Confirm Reimplementation`)
- Creepers' explosions spawn at the center of them, instead of the bottom. (`TODO: Confirm Reimplementation`)
- Creepers have a chance to instantly explode when interacted with shears, the chance is higher when charged. (`TODO: Confirm Reimplementation`)
- Creepers explode when damaged by explosions. (`TODO: Confirm Reimplementation`)
- Creepers don't immediately stop fusing on sight being blocked. (`TODO: Confirm Reimplementation`)
- Creepers have a chance to be able to sense targets through opaque blocks. (`TODO: Confirm Reimplementation`)

### Witches

- Witches move faster. (`TODO: Confirm Reimplementation` - speed value is unknown)
- Witches predict their target's position before throwing potions. (`TODO: Confirm Reimplementation`)
- Witches try not to get too close to dangerous targets. (`TODO: Confirm Reimplementation`)
- Witches target villagers. **(Targeting added for `EntityVillager.class`)**
- Witches have a chance to be able to sense targets through opaque blocks. (`TODO: Confirm Reimplementation`)
- Witches try to flee from explosions. **(Implemented with `EntityAIFleeFromExplosion`)**
- Witches' base max health: **(Scales: 24.0 HP (T0), 28.0 HP (T1+))**

### Spiders

- Spiders have a high chance to spawn with a random positive potion effect after the first nether portal is constructed. **(T1+ Spiders have 1/16 chance for Speed, Strength, or Invisibility)**
- Cobwebs don't only slow down entities' move speed, but also make players hardly rotate their look direction. **(Player look rotation is slowed by a factor of 0.25F)**
- Spiders' base max health: **(Scales: 16.0 HP (T0), 20.0 HP (T1+))**

### Slimes

- One of the slimes that big slimes and medium core slimes split into on death becomes a core slime, whose texture is slightly different from ordinary ones. **(Slimes < size 4 have 1/4 chance to be designated a "Core" Slime)**
- Core slimes attract nearby ordinary slimes to approach them, and finally merge into them to become one larger slime. **(Fields `isMerging` and `mergeCooldownCounter` added for this mechanic)**

### Silverfish

- Infested stones generate in all biomes; the deeper, the more. (`TODO: Confirm Reimplementation` - Missing world gen mixin)
- When attacked, instead of taking damage, silverfish split into two, each of them with half the health of their mother. **(Splits into two new ones, each with half HP of the mother, and opposite momentum. Max Health scales: 8.0 HP (T0), 12.0 HP (T1+))**

### Zombie Pigmen

- Zombie pigmen are able to break certain types of blocks blocking the way to their attack target, determined by their held item. (`TODO: Confirm Reimplementation`)
- Zombie pigmen get invoked by creatures that are too close to them. **(Aggro range is 1.5 blocks (T0/T1) or 6.0 blocks (T2+))**

### Magma Cubes

- Small magma cubes melt into a puddle of lava on death. (`TODO: Confirm Reimplementation`)
- Magma cubes regenerate their health when touching lava. (`TODO: Confirm Reimplementation`)

### Blazes

- Blazes' fireballs cause explosions after the first wither is summoned. (`TODO: Confirm Reimplementation`)

### Ghasts

- Ghasts have a translucent look. (`TODO: Confirm Reimplementation`)
- Arrows and throwable items cannot collide with ghasts, instead fly through them. **(Collision prevented for `EntityThrowable` (e.g., snowballs, eggs, pearls))**
- Ghasts only take damage from fireballs and magic. (`TODO: Confirm Reimplementation` - Missing damage logic)
- Ghasts' fireballs cause larger explosions after the first wither is summoned. **(The fireball itself causes an *additional* small explosion (power 1) if progress is Tier 2+)**

### Withers

- Withers follow their attack target at a larger distance. **(Follow range increased to 60.0D)**
- Withers sometimes summon wither skeletons when their health is above half. **(1/400 chance on AI update when HP > 50%)**
- Withers sometimes dash toward their attack target when their health is below half. **(1/200 chance on AI update when HP < 50%)**

### Endermen

- Endermen sometimes try to teleport their attack target when they cannot reach them. (`TODO: Confirm Reimplementation`)
- Players' gloom level rapidly increase when starring at an enderman. (`TODO: Confirm Reimplementation`)
- Players' gloom level rapidly increase when being too close to an enderman. (`TODO: Confirm Reimplementation`)

### Ender Dragons

- When hit, instead of disappearing, ender crystals become dried, in this state they cannot heal the ender dragon. (`TODO: Confirm Reimplementation`)
- When hit, ender crystals avenge the attacker with a lightning bolt. (`TODO: Confirm Reimplementation`)
- Ender crystals try to charge nearby dried crystals, helping them restore vitality. (`TODO: Confirm Reimplementation`)
- Players' gloom level rapidly increase when being close to an ender dragon. (`TODO: Confirm Reimplementation`)

### Spawners

- Spawners curse nearby players with negative potion effects when broken. (`TODO: Confirm Reimplementation`)

### Passive Mobs

- Villagers and pigs try to flee from explosions. **(CONFIRMED for Villagers with `EntityAIFleeFromExplosion`)**
- Pigs counterattack at high health. (`TODO: Confirm Reimplementation`)
- Some cows counterattack to protect weaker ones in the herd. (`TODO: Confirm Reimplementation`)
- Players' view is locked on first person when head crabbed by a squid. (`TODO: Confirm Reimplementation`)
- Animals have enhanced fleeing AI. (`TODO: Confirm Reimplementation`)
- Animals panic when pushed by a player. (`TODO: Confirm Reimplementation`)
- Players' reputation decrease if they attack villagers in a village. (`TODO: Confirm Reimplementation`)
- Villagers refuse to trade with players who have low reputation. **(Refuse if village reputation is < -5)**
- **Sheep counterattack** at high health. **(Flee behavior removed and replaced with `AnimalCombatBehavior` against `EntityZombie.class`)**