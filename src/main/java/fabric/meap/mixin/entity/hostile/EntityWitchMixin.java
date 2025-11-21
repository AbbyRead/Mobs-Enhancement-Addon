package fabric.meap.mixin.entity.hostile;

import net.minecraft.src.*;
import btw.community.abbyread.meap.ai.EntityAIFleeFromExplosion;
import btw.community.abbyread.meap.core.MEAUtils;
import btw.community.abbyread.meap.extension.EntityMobExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWitch.class)
public abstract class EntityWitchMixin extends EntityMob implements IRangedAttackMob {
    @SuppressWarnings("unused")
    private EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addFleeFromExplosionTask(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);

        this.tasks.addTask(1, new EntityAIFleeFromExplosion(this, 0.375F, 4.0F));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(
                this,
                EntityVillager.class,
                24.0F,
                0,
                ((EntityMobExtend)this).meap$getCanXray() == (byte)0
        ));
    }

    @Inject(method = "applyEntityAttributes", at = @At("RETURN"))
    private void modifyMaxHealth(CallbackInfo ci) {
        double baseHealth = 24.0;

        if (this.worldObj != null) {
            int tier = MEAUtils.getGameProgressMobsLevel(this.worldObj);
            if (tier > 0) {
                baseHealth = 28.0;
            }
        }

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setAttribute(baseHealth);

        // Heal to new max HP since applyEntityAttributes is called at construction time
        this.setHealth((float) baseHealth);
    }
}
