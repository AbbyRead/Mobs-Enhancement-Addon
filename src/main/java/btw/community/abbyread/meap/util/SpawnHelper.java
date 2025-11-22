package btw.community.abbyread.meap.util;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpawnHelper {

	static final Logger LOGGER = LogManager.getLogger("MEAP");

	/**
	 * Spawns any vanilla mob dynamically in the world.
	 *
	 * @param world       The world instance
	 * @param entityClass The class of the entity to spawn (e.g., EntityZombie.class)
	 * @param x           X-coordinate
	 * @param y           Y-coordinate
	 * @param z           Z-coordinate
	 * @return The spawned entity, or null if an error occurred
	 */
	public static EntityLiving spawnVanillaMob(World world, Class<? extends EntityLiving> entityClass,
	                                           double x, double y, double z, boolean initializeNaturally) {
		try {
			EntityLiving entity = entityClass.getConstructor(World.class).newInstance(world);

			float yaw = world.rand.nextFloat() * 360.0F;
			float pitch = 0.0F;

			entity.setLocationAndAngles(x, y, z, yaw, pitch);

			if (initializeNaturally) {
				entity.onSpawnWithEgg(null);
			}

			return entity;

		} catch (Exception e) {
			LOGGER.error("Failed to spawn entity: {}", entityClass.getName(), e);
			return null;
		}
	}
}
