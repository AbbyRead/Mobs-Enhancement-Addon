package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityCreature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityCreature.class)
public interface EntityCreatureAccess {

	@Invoker("func_110174_bM")
	float invokeGetMaximumHomeDistance();

	@Accessor("maximumHomeDistance")
	float getMaximumHomeDistance();

	@Accessor("homePosition")
	default ChunkCoordinates getHomePosition() {
		return null;
	}
}
