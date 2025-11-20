package net.pottx.mobsenhancement.mixin.access;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unused")
@Mixin(EntityWither.class)
public interface EntityWitherAccess {

    @Accessor("field_82223_h")
    int[] getNextHeadAttackTime();

    @Accessor("field_82224_i")
    int[] getIdleHeadTicks();

    @Accessor("field_82222_j")
    int getBlockBreakCounter();

    @Accessor("field_82222_j")
    void setBlockBreakCounter(int blockBreakCounter);

    @Accessor("attackEntitySelector")
    static IEntitySelector getValidTargetSelector() {
        throw new AssertionError();
    }

    @Invoker("func_82209_a")
    void invokeShootSkullAt(
            int headIndex,
            double targetX,
            double targetY,
            double targetZ,
            boolean isCharged
    );

    @Invoker("func_82216_a")
    void invokeFireSkullAtEntity(int headIndex, EntityLivingBase target);

    @Invoker("func_82211_c")
    void invokeSetHeadTarget(int headIndex, int targetEntityId);

    @Invoker("func_82212_n")
    int invokeGetSpawnInvulnerabilityTime();

    @Invoker("func_82215_s")
    void invokeSetSpawnInvulnerabilityTime(int time);

}