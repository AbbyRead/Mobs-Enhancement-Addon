package net.pottx.mobsenhancement.mixin.access;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPigZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPigZombie.class)
public interface EntityPigZombieAccess {
	@Invoker("becomeAngryAt")
	void becomeAngryAt(Entity entity);
}
