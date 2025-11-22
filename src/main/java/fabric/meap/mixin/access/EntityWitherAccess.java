package fabric.meap.mixin.access;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unused")
@Mixin(EntityWither.class)
public interface EntityWitherAccess {

    @Accessor("field_82223_h")
    int[] meap$getNextHeadAttackTime();

    @Accessor("field_82224_i")
    int[] meap$getIdleHeadTicks();

    @Accessor("field_82222_j")
    int meap$getBlockBreakCounter();

    @Accessor("field_82222_j")
    void meap$setBlockBreakCounter(int blockBreakCounter);

    @Accessor("attackEntitySelector")
    static IEntitySelector meap$getValidTargetSelector() {
        throw new AssertionError();
    }

    @Accessor("attackEntitySelector")
    static IEntitySelector meap$getAttackEntitySelector() {
        throw new AssertionError();
    }

    @Invoker("func_82209_a")
    void meap$shootSkullAt(
            int headIndex,
            double targetX,
            double targetY,
            double targetZ,
            boolean isCharged
    );

    @Invoker("func_82216_a")
    void meap$fireSkullAtEntity(int headIndex, EntityLivingBase target);

    @Invoker("func_82211_c")
    void meap$setHeadTarget(int headIndex, int targetEntityId);

    @Invoker("func_82212_n")
    int meap$getSpawnInvulnerabilityTime();

    @Invoker("func_82215_s")
    void meap$setSpawnInvulnerabilityTime(int time);
}