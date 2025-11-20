package net.pottx.mobsenhancement.mixin.access;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLiving.class)
public interface EntityLivingAccess {

    @SuppressWarnings("unused")
    @Accessor("attackTarget")
    void setAttackTarget(EntityLivingBase attackTarget);

    @Invoker("addRandomArmor")
    void invokeAddRandomArmor();

    @Accessor("attackTarget")
    EntityLivingBase getAttackTarget();

    @Accessor("navigator")
    PathNavigate getNavigator();

    @Accessor("senses")
    EntitySenses getSenses();

    @Accessor("lookHelper")
    EntityLookHelper getLookHelper();

    @SuppressWarnings("unused")
    @Accessor("tasks")
    EntityAITasks getTasks();

}
