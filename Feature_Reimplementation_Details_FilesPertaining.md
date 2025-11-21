This is the list of confirmed features with the exact file path or class name where the implementation was found in the provided code snippets.

***

## ✅ Confirmed Features with Implementation Files

| Feature Category | Feature/Behavior | Implementation File / Class |
| :--- | :--- | :--- |
| **All Mobs** | **Health Scaling** (Tiered progression) | `mixin/EntityZombieMixin.java`, `mixin/EntitySpiderMixin.java`, `mixin/EntityWitchMixin.java`, `mixin/EntitySilverfishMixin.java` |
| | **Targeting Range** (e.g., Zombies/Witches/Wither) | `mixin/EntityZombieMixin.java`, `mixin/EntityWitchMixin.java`, `mixin/EntityWitherMixin.java` |
| | **Zombie AI Cleanup** | `mixin/EntityZombieMixin.java` |
| **Zombies** | **Block Breaking** (`EntityAIBreakBlock`) | `mixin/EntityZombieMixin.java` |
| | **Health Scaling** (16/20/24 HP) | `mixin/EntityZombieMixin.java` |
| | **X-Ray Sight** (Targeting check) | `mixin/EntityZombieMixin.java` |
| | **Transformation on Sun Death** (Into Skeleton) | `mixin/EntityZombieMixin.java` |
| | **Targeting Expansion** (Creatures/Villagers) | `mixin/EntityZombieMixin.java` |
| **Skeletons** | **Sun Immunity** | `mixin/EntitySkeletonMixin.java` |
| | **Wither Skeleton Spawning** (Below Y=32) | `mixin/EntitySkeletonMixin.java` |
| | **Accuracy Scaling** (Deviation reduction) | `mixin/EntitySkeletonMixin.java` |
| | **Bow Durability** (Replaced on break) | `mixin/EntitySkeletonMixin.java` |
| | **Torch Breaking** (`SkeletonBreakTorchBehavior`) | `mixin/EntitySkeletonMixin.java` |
| **Witches** | **Health Scaling** (24/28 HP) | `mixin/EntityWitchMixin.java` |
| | **Targeting** (`EntityVillager.class`) | `mixin/EntityWitchMixin.java` |
| | **Evasion** (`EntityAIFleeFromExplosion`) | `mixin/EntityWitchMixin.java` |
| **Spiders** | **Potion Effects** (Speed, Strength, Invisibility) | `mixin/EntitySpiderMixin.java` |
| | **Cobweb Effect** (Look rotation slow) | `mixin/EntityRendererMixin.java` |
| | **Health Scaling** (16/20 HP) | `mixin/EntitySpiderMixin.java` |
| **Slimes** | **Core/Merging** (Fields and 1/4 chance) | `mixin/EntitySlimeMixin.java` |
| **Silverfish** | **Splitting** (`meap$split()`) | `mixin/EntitySilverfishMixin.java` |
| | **Health Scaling** (8/12 HP) | `mixin/EntitySilverfishMixin.java` |
| **Zombie Pigmen**| **Proximity Aggro** (1.5/6.0 range) | `mixin/EntityPigZombieMixin.java` |
| **Ghasts** | **Projectile Immunity** (`EntityThrowable`) | `mixin/EntityThrowableMixin.java` |
| | **Fireball Explosion** (Additional small blast) | `mixin/EntitySmallFireballMixin.java` |
| **Withers** | **Follow Range** (60.0D) | `mixin/EntityWitherMixin.java` |
| | **Minion Summon** (1/400 chance) | `mixin/EntityWitherMixin.java` |
| | **Dash Attack** (1/200 chance) | `mixin/EntityWitherMixin.java` |
| **Passive Mobs**| **Villager Trading** (Reputation < -5) | `mixin/EntityVillagerMixin.java` |
| | **Villager Evasion** (`EntityAIFleeFromExplosion`) | `mixin/EntityVillagerMixin.java` |
| | **Sheep Combat** (`AnimalCombatBehavior`) | `mixin/EntitySheepMixin.java` |

***

## ❌ Remaining Unconfirmed Features

These features lack any corresponding code, meaning the implementation files are still missing.

* **Ghasts:** Damage immunity to non-fireball/magic.
* **Ender Crystals:** Revenge lightning, and charging mechanic.
* **Endermen:** Target teleportation, gloom level effects.
* **Skeletons:** Exact Max Health and Speed attributes (missing `applyEntityAttributes` in `EntitySkeletonMixin`).
* **All Mobs:** Increased melee attack reach with tools, group spawning after End portal.
* **Silverfish:** Infested stone world generation in all biomes.
* **Creepers, Magma Cubes, Blazes, and various Passive Mob behaviors** (as detailed in the full list).