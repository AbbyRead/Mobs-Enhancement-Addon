package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.behavior.AnimalFleeBehavior;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.AnimalCombatBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySheep.class)
public class EntitySheepMixin extends EntityAnimal
{
    private EntitySheepMixin(World par1World) {
        super(par1World);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable var1) {
        return null; // TODO: Replace with actual implementation
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addExtraTasks(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(AnimalFleeBehavior.class);
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);

        tasks.addTask(1, new AnimalCombatBehavior(this, 0.32F, 0.38F, EntityZombie.class, 8));
    }
}
