package fabric.meap.mixin.access;

import net.minecraft.src.EntitySlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntitySlime.class)
public interface EntitySlimeAccess {
	@Invoker("setSlimeSize")
	void invokeSetSlimeSize(int size);

	@Invoker("createInstance")
	EntitySlime getCreateInstance();
}
