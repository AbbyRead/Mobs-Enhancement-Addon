package fabric.meap.mixin.entity.hostile;

import btw.community.abbyread.meap.util.SpawnHelper;
import btw.community.abbyread.meap.util.WitherSkeletonSpawnHelper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import btw.community.abbyread.meap.behavior.WitherDashBehavior;
import btw.community.abbyread.meap.behavior.WitherSummonMinionBehavior;
import btw.community.abbyread.meap.extend.EntityWitherExtend;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob implements EntityWitherExtend {

    @SuppressWarnings("unused")
    private EntityWitherMixin(World world) { super(world); }

    @Unique
    private final EntityWither self = (EntityWither)(Object)this;

    @Unique
    private boolean isDoingSpecialAttack;

    @Override
    public boolean meap$getIsDoingSpecialAttack() { return isDoingSpecialAttack; }

    @Override
    public void meap$setIsDoingSpecialAttack(boolean value) { isDoingSpecialAttack = value; }

    // Queue for deferred skeleton spawns
    @Unique
    private final List<double[]> skeletonSpawnQueue = new ArrayList<>();

    // --------------------------------------------------------------------
    // Constructor injection: AI tasks
    // --------------------------------------------------------------------
    @Inject(method = "<init>", at = @At("TAIL"))
    private void initMixin(CallbackInfo ci) {
        // Add special-attack AI tasks
        this.tasks.addTask(1, new WitherSummonMinionBehavior(self));
        this.tasks.addTask(1, new WitherDashBehavior(self));
    }

    // --------------------------------------------------------------------
    // Inject special attack behavior on tick
    // --------------------------------------------------------------------
    @Inject(method = "updateAITasks", at = @At("TAIL"))
    private void injectSpecialAttackBehavior(CallbackInfo ci) {
        EntityWither w = self;

// -------------------------------------------------------------
// 1. Summon Wither Skeletons at >50% HP (rarely)
// -------------------------------------------------------------
        if (w.getHealth() > w.getMaxHealth() * 0.5f && w.rand.nextInt(400) == 0) {

            // Step 1: Determine safe spawn coordinates
            double x = w.posX + (w.worldObj.rand.nextDouble() - 0.5) * 2.0;
            double z = w.posZ + (w.worldObj.rand.nextDouble() - 0.5) * 2.0;
            int y = w.worldObj.getTopSolidOrLiquidBlock((int) x, (int) z); // safe ground Y

            // Step 2: Spawn the entity
            for (double[] pos : skeletonSpawnQueue) {
                EntitySkeleton sk = (EntitySkeleton) SpawnHelper.spawnVanillaMob(
                        self.worldObj, EntitySkeleton.class, pos[0], pos[1], pos[2], true
                );
                if (sk != null) {
                    sk.setSkeletonType(1);        // Step 1: set type
                    sk.onSpawnWithEgg(null);      // Step 2: initialize AI/attributes
                    self.worldObj.spawnEntityInWorld(sk); // Step 3: add to world
                }
            }
            skeletonSpawnQueue.clear();
        }

        // -------------------------------------------------------------
        // 2. Dash attack at <50% HP (rarely)
        // -------------------------------------------------------------
        if (w.getHealth() < w.getMaxHealth() * 0.5f) {
            EntityLivingBase target = w.getAttackTarget();
            if (target != null && w.rand.nextInt(200) == 0) {
                double dx = target.posX - w.posX;
                double dz = target.posZ - w.posZ;
                double dist = Math.sqrt(dx * dx + dz * dz);

                if (dist > 0.1) {
                    dx /= dist;
                    dz /= dist;
                    w.addVelocity(dx * 2.0, 0.1, dz * 2.0);
                }
            }
        }
    }

    // --------------------------------------------------------------------
    // Spawn queued skeletons safely at the start of onLivingUpdate
    // --------------------------------------------------------------------
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void spawnQueuedSkeletons(CallbackInfo ci) {
        for (double[] pos : skeletonSpawnQueue) {
            EntitySkeleton sk = new EntitySkeleton(self.worldObj);
            sk.setSkeletonType(1); // Wither Skeleton
            sk.setPosition(pos[0], pos[1], pos[2]);
            self.worldObj.spawnEntityInWorld(sk);
        }
        skeletonSpawnQueue.clear();
    }
}
