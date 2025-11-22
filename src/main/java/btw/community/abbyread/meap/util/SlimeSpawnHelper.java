package btw.community.abbyread.meap.util;

import net.minecraft.src.EntitySlime;
import net.minecraft.src.World;

import fabric.meap.mixin.access.EntitySlimeAccess;
import btw.community.abbyread.meap.extend.EntitySlimeExtend;

/**
 * A safe, 1.6.4-correct helper for splitting and spawning slimes
 * without causing NBT or DataWatcher side effects.
 */
public final class SlimeSpawnHelper {

	private SlimeSpawnHelper() {}

	/**
	 * Creates a child slime using the correct vanilla 1.6.4 factory path.
	 * The parent MUST be a real slime.
	 */
	public static EntitySlime createChildSlime(EntitySlime parent) {
		EntitySlimeAccess access = (EntitySlimeAccess) parent;
		World world = parent.worldObj;

		// Vanilla factory constructor
		EntitySlime child = access.getCreateInstance();

		// Assign world
		child.worldObj = world;

		// Proper AI + attribute initialization
		child.onSpawnWithEgg(null);

		// Reset extension flags
		EntitySlimeExtend ext = (EntitySlimeExtend) child;
		ext.meap$setIsCore((byte) 0);
		ext.meap$setIsMerging(false);
		ext.meap$setIsMagma(false);

		return child;
	}

	/**
	 * Applies size to a child slime without triggering injected code.
	 */
	public static void applyChildSize(EntitySlime child, int size) {
		((EntitySlimeAccess) child).invokeSetSlimeSize(size);
	}
}
