package btw.community.abbyread.meap.util;

import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntityWither;
import net.minecraft.src.World;

import btw.community.abbyread.meap.extend.EntityWitherExtend;
import btw.community.abbyread.meap.extend.EntitySkeletonExtend;

/**
 * A safe, 1.6.4-compatible helper to spawn Wither Skeletons from a Wither.
 */
public final class WitherSkeletonSpawnHelper {

	private WitherSkeletonSpawnHelper() {}

	/**
	 * Creates a new Wither Skeleton safely, without triggering attribute NPEs.
	 */
	public static EntitySkeleton createWitherSkeleton(World world) {
		// Vanilla constructor
		EntitySkeleton skeleton = new EntitySkeleton(world);

		// Proper AI + attribute initialization
		skeleton.onSpawnWithEgg(null);

		// Reset mixin extension flags
		EntitySkeletonExtend ext = (EntitySkeletonExtend) skeleton;
		ext.meap$setIsBreakingTorch(false);

		return skeleton;
	}

	/**
	 * Places the skeleton at the target coordinates with random offset.
	 */
	public static void spawnWitherSkeleton(World world, double x, double y, double z) {
		EntitySkeleton skeleton = new EntitySkeleton(world);
        skeleton.setSkeletonType(1);
		double dx = x + (world.rand.nextDouble() - 0.5) * 2.0;
		double dz = z + (world.rand.nextDouble() - 0.5) * 2.0;

        skeleton.setLocationAndAngles(dx + dx, y, dz + dz,
		        world.rand.nextFloat() * 360F, 0F);
		world.spawnEntityInWorld(skeleton);
	}
}
