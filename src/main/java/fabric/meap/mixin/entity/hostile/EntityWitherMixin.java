package fabric.meap.mixin.entity.hostile;

import net.minecraft.src.*;
import btw.community.abbyread.meap.behavior.WitherDashBehavior;
import btw.community.abbyread.meap.behavior.WitherSummonMinionBehavior;
import btw.community.abbyread.meap.extension.EntityWitherExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob implements EntityWitherExtend {

    @SuppressWarnings("unused")
    private EntityWitherMixin(World world) { super(world); }

    // --------------------------------------------------------------------
    // Unique reference to avoid repeated casting
    // --------------------------------------------------------------------
    @Unique
    private final EntityWither self = (EntityWither)(Object)this;

    // --------------------------------------------------------------------
    // Track whether Wither is performing a special attack
    // --------------------------------------------------------------------
    @Unique
    private boolean isDoingSpecialAttack;

    @Override
    public boolean meap$getIsDoingSpecialAttack() { return isDoingSpecialAttack; }

    @Override
    public void meap$setIsDoingSpecialAttack(boolean value) { isDoingSpecialAttack = value; }

    // --------------------------------------------------------------------
    // Constructor injection: follow distance + AI tasks
    // --------------------------------------------------------------------
    @Inject(method = "<init>", at = @At("TAIL"))
    private void initMixin(CallbackInfo ci) {
        // Increase tracking distance
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(60.0D);

        // Add special-attack AI tasks
        this.tasks.addTask(1, new WitherSummonMinionBehavior(self));
        this.tasks.addTask(1, new WitherDashBehavior(self));
    }

    // --------------------------------------------------------------------
    // updateAITasks injection: skeleton summon + dash attack
    // --------------------------------------------------------------------
    @Inject(method = "updateAITasks", at = @At("TAIL"))
    private void injectSpecialAttackBehavior(CallbackInfo ci) {
        EntityWither w = self;

        // -------------------------------------------------------------
        // 1. Summon skeletons at >50% HP (rarely)
        // -------------------------------------------------------------
        if (w.getHealth() > w.getMaxHealth() * 0.5f && w.rand.nextInt(400) == 0) {
            EntitySkeleton sk = new EntitySkeleton(w.worldObj);
            if (sk.setSkeletonType(1)) {
                double x = w.posX + (w.rand.nextDouble() - 0.5) * 2.0;
                double y = w.posY;
                double z = w.posZ + (w.rand.nextDouble() - 0.5) * 2.0;
                sk.setPosition(x, y, z);
                w.worldObj.spawnEntityInWorld(sk);
            }
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
                    // Safe velocity burst
                    w.addVelocity(dx * 2.0, 0.1, dz * 2.0);
                }
            }
        }
    }
}
