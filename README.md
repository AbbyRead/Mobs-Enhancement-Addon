# Mobs Enhancement Abby Port

This is a port of Mobs Enhancement Addon (MEA) to Better Than Wolves 3.0.0.  MEA aims to slightly strengthen mobs, adding difficulty to the game.

## Features

### All Mobs

- ⬛ Mobs enjoy increased melee attack reach distance when holding a tool.
- ⬛ Mobs' senses are strengthened, with further target distance and more points to check, instead of only doing an eye-to-eye check.
- ⬛ Mobs cannot see entities behind them unless they are very close.
- ⬛ Mobs can see through transparent blocks such as glass, leaves.
- ⬛ Mobs no longer try to stare at the closest player.
- ⬛ Hostile mobs' max health increase when the first nether portal is constructed.
- ⬛ Nether and undead mobs' max health increase when the first wither is summoned.
- ⬛ Hostile mobs have a higher chance to spawn in groups after the first end portal is activated.

### Zombies

- ⬛ Zombies are able to break certain types of blocks blocking the way to their attack target, determined by their held item.
- ⬛ Zombies' base max health is decreased from 20 to 16.
- ⬛ Zombies have a chance to be able to sense targets through opaque blocks.
- ⬛ Zombies transform into a skeleton of half its max health when die of sunlight, 

### Skeletons

- ⬛ Skeletons move faster.
- ⬛ Skeletons no longer burn under sunlight.
- ⬛ Skeletons have a chance to spawn with a melee weapon instead of a bow.
- ⬛ Skeletons have a chance to spawn as a wither skeleton in the overworld at a low Y level after the first nether portal is constructed.
- ⬛ Skeletons predict their target's position before ranged attacks.
- ⬛ Skeletons' accuracy increases when the first nether portal is constructed, and when the first wither is summoned.
- ⬛ Skeletons' bows have limited durability, which increases when the first nether portal is constructed, and when the first wither is summoned.
- ⬛ Skeletons try not to get too close to dangerous targets when holding a bow.
- ⬛ Skeletons flee from dangerous targets when their health is low.
- ⬛ Skeletons' base max health is decreased from 20 to 12.
- ⬛ Skeletons target villagers.
- ⬛ Skeletons try to break nearby burning torches.
- ⬛ Skeletons have a chance to be able to sense targets through opaque blocks.
- ⬛ Skeletons try to flee from explosions.

### Creepers

- ⬛ Creepers have a chance to spawn charged after the first nether portal is constructed.
- ⬛ Creepers' base fuse time is decreased from 1.5 seconds to 1.25 second.
- ⬛ Creepers' fuse time decrease when the first nether portal is constructed.
- ⬛ Creepers' explosions spawn at the center of them, instead of the bottom.
- ⬛ Creepers have a chance to instantly explode when interacted with shears, the chance is higher when charged.
- ⬛ Creepers explode when damaged by explosions.
- ⬛ Creepers don't immediately stop fusing on sight being blocked.
- ⬛ Creepers have a chance to be able to sense targets through opaque blocks.

### Witches

- ⬛ Witches move faster.
- ⬛ Witches predict their target's position before throwing potions.
- ⬛ Witches try not to get too close to dangerous targets.
- ⬛ Witches target villagers.
- ⬛ Witches have a chance to be able to sense targets through opaque blocks.
- ⬛ Witches try to flee from explosions.

### Spiders

- ⬛ Spiders have a high chance to spawn with a random positive potion effect after the first nether portal is constructed.
- ⬛ Cobwebs don't only slow down entities' move speed, but also make players hardly rotate their look direction.

### Slimes

- ✅ One of the slimes that big slimes and medium core slimes split into on death becomes a core slime, whose texture is slightly different from ordinary ones.
- ✅ Core slimes attract nearby ordinary slimes to approach them, and finally merge into them to become one larger slime.

Currently:
- ⬛ Core slime has missing texture magenta
- ⬛ they don't merge.

### Silverfish

- ⬛ Infested stones generate in all biomes; the deeper, the more.
- ⬛ When attacked, instead of taking damage, silverfish split into two, each of them with half the health of their mother.

### Zombie Pigmen

- ⬛ Zombie pigmen are able to break certain types of blocks blocking the way to their attack target, determined by their held item.
- ⬛ Zombie pigmen get invoked by creatures that are too close to them.

### Magma Cubes

- ⬛ Small magma cubes melt into a puddle of lava on death.
- ⬛ Magma cubes regenerate their health when touching lava.

### Blazes

- ⬛ Blazes' fireballs cause explosions after the first wither is summoned.

### Ghasts

- ⬛ Ghasts have a translucent look.
- ⬛ Arrows and throwable items cannot collide with ghasts, instead fly through them.
- ⬛ Ghasts only take damage from fireballs and magic.
- ⬛ Ghasts' fireballs cause larger explosions after the first wither is summoned.

### Withers

- ✅ Withers follow their attack target at a larger distance.
- ✅ Withers sometimes summon wither skeletons when their health is above half.
- ✅ Withers sometimes dash toward their attack target when their health is below half.

### Endermen

- ⬛ Endermen sometimes try to teleport their attack target when they cannot reach them.
- ⬛ Players' gloom level rapidly increase when starring at an enderman.
- ⬛ Players' gloom level rapidly increase when being too close to an enderman.

### Ender Dragons

- ⬛ When hit, instead of disappearing, ender crystals become dried, in this state they cannot heal the ender dragon.
- ⬛ When hit, ender crystals avenge the attacker with a lightning bolt.
- ⬛ Ender crystals try to charge nearby dried crystals, helping them restore vitality.
- ⬛ Players' gloom level rapidly increase when being close to an ender dragon.

### Spawners

- ⬛ Spawners curse nearby players with negative potion effects when broken.

### Passive Mobs

- ⬛ Villagers and pigs try to flee from explosions.
- ⬛ Pigs counterattack at high health.
- ⬛ Some cows counterattack to protect weaker ones in the herd.
- ⬛ Players' view is locked on first person when head crabbed by a squid.
- ⬛ Animals have enhanced fleeing AI.
- ⬛ Animals panic when pushed by a player.
- ⬛ Players' reputation decrease if they attack villagers in a village.
- ⬛ Villagers refuse to trade with players who have low reputation.

## Confirmed Reimplementations (MEA Files listed):

- ✅ MobsEnhancementAddon.java
- ⬛ access/EntityArrowAccess.java
- ⬛ access/EntityEnderCrystalAccess.java
- ⬛ access/EntityLivingAccess.java
- ⬛ access/EntityMobAccess.java
- ⬛ access/EntityPlayerAccess.java
- ⬛ access/SkeletonEntityAccess.java
- ✅ access/SlimeEntityAccess.java
- ✅ access/WitherEntityAccess.java
- ⬛ access/ZombieEntityAccess.java
- ⬛ AnimalCombatBehavior.java
- ⬛ EntityAIBreakBlock.java
- ⬛ EntityAIFleeFromEnemy.java
- ⬛ EntityAIFleeFromExplosion.java
- ⬛ EntityAISmartArrowAttack.java
- ⬛ EntityAISmartAttackOnCollide.java
- ✅ MEAEffectManager.java
- ⬛ MEAUtils.java
- ⬛ mixin/BiomeDecoratorMixin.java
- ⬛ mixin/BlazeEntityMixin.java
- ⬛ mixin/CowEntityMixin.java
- ⬛ mixin/CreeperEntityAccess.java
- ⬛ mixin/CreeperEntityMixin.java
- ⬛ mixin/CreeperSwellBehaviorMixin.java
- ⬛ mixin/EndermanEntityMixin.java
- ⬛ mixin/EntityAccess.java
- ⬛ mixin/EntityAILookIdleMixin.java
- ⬛ mixin/EntityAITargetMixin.java
- ⬛ mixin/EntityAITradePlayerMixin.java
- ⬛ mixin/EntityAnimalMixin.java
- ⬛ mixin/EntityArrowMixin.java
- ⬛ mixin/EntityCreeperAccess.java
- ⬛ mixin/EntityCreeperMixin.java
- ⬛ mixin/EntityDragonMixin.java
- ⬛ mixin/EntityEnderCrystalMixin.java
- ⬛ mixin/EntityGhastAccess.java
- ⬛ mixin/EntityLivingAccess.java
- ⬛ mixin/EntityLivingMixin.java
- ⬛ mixin/EntityMobMixin.java
- ⬛ mixin/EntityPlayerMixin.java
- ⬛ mixin/EntityPlayerMPMixin.java
- ⬛ mixin/EntityRendererMixin.java
- ⬛ mixin/EntitySilverfishMixin.java
- ⬛ mixin/EntitySkeletonMixin.java
- ⬛ mixin/EntitySmallFireballMixin.java
- ⬛ mixin/EntityThrowableMixin.java
- ⬛ mixin/EntityVillagerMixin.java
- ⬛ mixin/EntityWitchMixin.java
- ✅ mixin/EntityWitherAccess.java
- ⬛ mixin/EntityWitherMixin.java
- ⬛ mixin/GhastEntityMixin.java
- ⬛ mixin/MagmaCubeEntityMixin.java
- ⬛ mixin/MinecraftMixin.java
- ⬛ mixin/MobSpawnerBlockMixin.java
- ⬛ mixin/PigEntityMixin.java
- ⬛ mixin/RenderEnderCrystalMixin.java
- ⬛ mixin/RenderGhastMixin.java
- ⬛ mixin/SheepEntityMixin.java
- ⬛ mixin/SimpleWanderBehaviorMixin.java
- ⬛ mixin/SkeletonEntityMixin.java
- ✅ mixin/SlimeEntityMixin.java
- ⬛ mixin/SpawnerAnimalsMixin.java
- ⬛ mixin/SpiderEntityMixin.java
- ⬛ mixin/VillagerEntityMixin.java
- ⬛ mixin/WitchEntityMixin.java
- ✅ mixin/WitherEntityMixin.java
- ⬛ mixin/ZombieEntityMixin.java
- ⬛ mixin/ZombiePigmanEntityMixin.java
- ⬛ SkeletonBreakTorchBehavior.java
- ✅ WitherDashBehavior.java
- ✅ WitherSummonMinionBehavior.java
- ✅ resources/fabric.mod.json
- ✅ resources/meatextures/core_slime_1.png
- ✅ resources/meatextures/core_slime_2.png
- ✅ resources/meatextures/crystal_dried.png
- ✅ resources/meatextures/ghast.png
- ✅ resources/meatextures/ghast_fire.png
- ✅ resources/mobsenhancement/icon.png
- ✅ resources/mobsenhancement/thumbnail.png
- ✅ resources/mobsenhancement.mixins.json

## Project Structure (Outdated from 24-Hour Attempt):
```
src/main/
├── java/btw/community/abbyread/meap/
│   ├── MobsEnhancementAddon.java
│   ├── ai/
│   │   ├── EntityAIBreakBlock.java
│   │   ├── EntityAIFleeFromEnemy.java
│   │   ├── EntityAIFleeFromExplosion.java
│   │   ├── EntityAISmartArrowAttack.java
│   │   └── EntityAISmartAttackOnCollide.java
│   ├── behavior/
│   │   ├── AnimalCombatBehavior.java
│   │   ├── SkeletonBreakTorchBehavior.java
│   │   ├── WitherDashBehavior.java
│   │   └── WitherSummonMinionBehavior.java
│   ├── core/
│   │   ├── MEAEffectManager.java
│   │   └── MEAUtils.java
│   └── extend/
│       ├── EntityArrowExtend.java
│       ├── EntityEnderCrystalExtend.java
│       ├── EntityLivingBaseExtend.java
│       ├── EntityLivingExtend.java
│       ├── EntityMobExtend.java
│       ├── EntityPlayerExtend.java
│       ├── EntitySilverfishExtend.java
│       ├── EntitySkeletonExtend.java
│       ├── EntitySlimeExtend.java
│       ├── EntityWitherExtend.java
│       └── EntityZombieExtend.java
├── java/fabric/meap/mixin/
│   ├── access/
│   │   ├── EntityAccess.java
│   │   ├── EntityCreatureAccess.java
│   │   ├── EntityCreeperAccess.java
│   │   ├── EntityGhastAccess.java
│   │   ├── EntityLivingAccess.java
│   │   ├── EntityLivingBaseAccess.java
│   │   ├── EntityPigZombieAccess.java
│   │   └── EntityWitherAccess.java
│   ├── ai/
│   │   ├── CreeperSwellBehaviorMixin.java
│   │   ├── EntityAILookIdleMixin.java
│   │   ├── EntityAITargetMixin.java
│   │   ├── EntityAITradePlayerMixin.java
│   │   └── SimpleWanderBehaviorMixin.java
│   ├── entity/
│   │   ├── generic/
│   │   │   ├── EntityAnimalMixin.java
│   │   │   ├── EntityLivingBaseMixin.java
│   │   │   ├── EntityLivingMixin.java
│   │   │   ├── EntityMixin.java
│   │   │   ├── EntityMobMixin.java
│   │   │   ├── EntityPlayerMPMixin.java
│   │   │   └── EntityPlayerMixin.java
│   │   ├── hostile/
│   │   │   ├── EntityBlazeMixin.java
│   │   │   ├── EntityCreeperMixin.java
│   │   │   ├── EntityDragonMixin.java
│   │   │   ├── EntityEnderCrystalMixin.java
│   │   │   ├── EntityEndermanMixin.java
│   │   │   ├── EntityGhastMixin.java
│   │   │   ├── EntityMagmaCubeMixin.java
│   │   │   ├── EntityPigZombieMixin.java
│   │   │   ├── EntitySilverfishMixin.java
│   │   │   ├── EntitySkeletonMixin.java
│   │   │   ├── EntitySlimeMixin.java
│   │   │   ├── EntitySpiderMixin.java
│   │   │   ├── EntityWitchMixin.java
│   │   │   ├── EntityWitherMixin.java
│   │   │   └── EntityZombieMixin.java
│   │   ├── passive/
│   │   │   ├── EntityCowMixin.java
│   │   │   ├── EntityPigMixin.java
│   │   │   ├── EntitySheepMixin.java
│   │   │   └── EntityVillagerMixin.java
│   │   └── projectile/
│   │       ├── EntityArrowMixin.java
│   │       ├── EntitySmallFireballMixin.java
│   │       └── EntityThrowableMixin.java
│   ├── render/
│   │   ├── EntityRendererMixin.java
│   │   ├── RenderEnderCrystalMixin.java
│   │   ├── RenderGhastMixin.java
│   │   └── RenderSlimeMixin.java
│   └── world/
│       ├── BiomeDecoratorMixin.java
│       ├── MinecraftMixin.java
│       ├── MobSpawnerBlockMixin.java
│       └── SpawnerAnimalsMixin.java
└── resources/
    ├── assets/
    │   ├── mea_textures/
    │   │   ├── core_slime_1.png
    │   │   ├── core_slime_2.png
    │   │   ├── crystal_dried.png
    │   │   ├── ghast.png
    │   │   └── ghast_fire.png
    │   └── mod_meta/
    │       ├── mea-faithful_icon.png
    │       ├── mea-faithful_icon_LAYERS.psd
    │       └── original_mea_project_thumbnail.png
    ├── fabric.mod.json
    └── meap.mixins.json
```