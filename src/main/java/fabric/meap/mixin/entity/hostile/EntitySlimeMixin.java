package fabric.meap.mixin.entity.hostile;

import fabric.meap.mixin.access.EntitySlimeAccess;
import net.minecraft.src.*;
import btw.community.abbyread.meap.util.MEAEffectManager;
import btw.community.abbyread.meap.util.SlimeSpawnHelper;
import btw.community.abbyread.meap.extend.EntitySlimeExtend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin extends EntityLiving implements IMob, EntitySlimeExtend, EntitySlimeAccess {
	@Unique EntitySlime self = (EntitySlime) (Object) this;


	@Unique
	public boolean isMagma;
	@Unique
	private static final int IS_CORE_DATA_WATCHER_ID = 20;
	@Unique
	public int mergeCooldownCounter;
	@Unique
	public boolean isMerging = false;

	public EntitySlimeMixin(World world) {
		super(world);
	}

	@Shadow
	public abstract int getSlimeSize();
	@Shadow
	protected abstract void setSlimeSize(int iSize);

	// -------------------------------------------------------------------
	// Interface Implementation
	// -------------------------------------------------------------------

	@Override public boolean meap$getIsMagma() { return this.isMagma; }
	@Override public void meap$setIsMagma(boolean value) { this.isMagma = value; }

	@Override public boolean meap$getIsMerging() { return this.isMerging; }
	@Override public void meap$setIsMerging(boolean value) { this.isMerging = value; }

	@Override public byte meap$getIsCore() {
		return this.dataWatcher.getWatchableObjectByte(IS_CORE_DATA_WATCHER_ID);
	}
	@Override public void meap$setIsCore(byte value) {
		this.dataWatcher.updateObject(IS_CORE_DATA_WATCHER_ID, value);
	}

	@Inject(
			method = "<init>",
			at = @At(value = "TAIL")
	)
	private void addIsCoreData(CallbackInfo ci) {
		this.isMagma = false;
		dataWatcher.addObject(IS_CORE_DATA_WATCHER_ID, (byte) 0);
		this.mergeCooldownCounter = 40;
		if (self.getSlimeSize() < 4 && this.rand.nextInt(4) == 0) this.meap$setIsCore((byte) 1);
	}

	@SuppressWarnings("DiscouragedShift")
	@Inject(method = "updateEntityActionState()V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/src/EntitySlime;faceEntity(Lnet/minecraft/src/Entity;FF)V",
			shift = At.Shift.BY, by = 2
	))
	private void doMergeCheck(CallbackInfo ci) {
		if (!this.isMagma) {
			if (this.meap$getIsCore() == (byte) 1) {
				this.mergeCooldownCounter--;
				if (mergeCooldownCounter <= 0) {
					@SuppressWarnings("unchecked")
					List<EntitySlime> nearby = this.worldObj.getEntitiesWithinAABB(EntitySlime.class, this.boundingBox.expand(12.0D, 6.0D, 12.0D)
					);
					// Filter out any slimes that are cores, different size, or dead
					nearby = nearby.stream()
							.filter(s -> ((EntitySlimeExtend)s).meap$getIsCore() == 0)   // Not a core
							.filter(s -> s.getSlimeSize() == self.getSlimeSize())        // Same size
							.filter(Entity::isEntityAlive)                               // Alive
							.toList();
					if (nearby.size() >= 2) {
						if (this.isMerging) {
							faceEntity(nearby.get(0), 10.0F, 20.0F);

							double checkRange = self.getSlimeSize() == 1 ? 0.75D : 1.0D;
							@SuppressWarnings("unchecked")
							List<EntitySlime> veryNearby = this.worldObj.getEntitiesWithinAABB(EntitySlime.class, this.boundingBox.expand(checkRange, checkRange, checkRange));

							veryNearby = veryNearby.stream()
									.filter(s -> ((EntitySlimeExtend)s).meap$getIsCore() == 0)  // Not a core
									.filter(s -> s.getSlimeSize() == self.getSlimeSize())       // Same size
									.filter(Entity::isEntityAlive)                                        // Alive
									.toList();                                                            // Returns an immutable list
							if (veryNearby.size() >= 2) {
								this.worldObj.playAuxSFX(MEAEffectManager.SLIME_MERGE_EFFECT_ID,
										MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);

								this.setSlimeSize(self.getSlimeSize() * 2);
								if (self.getSlimeSize() == 4) this.meap$setIsCore((byte) 0);
								for (int i = 0; i < 2; i++) {
									EntitySlimeExtend extend = (EntitySlimeExtend) veryNearby.get(i);
									extend.meap$simpleSetDead();
								}
								this.isMerging = false;
								this.mergeCooldownCounter = 40;
								for (Object closeSlime : nearby) {
									((EntitySlimeMixin) closeSlime).isMerging = false;
								}
							}
						} else {
							this.isMerging = true;
							for (Object closeSlime : nearby) {
								((EntitySlimeMixin) closeSlime).isMerging = true;
							}
						}
					} else if (this.isMerging) {
						this.isMerging = false;
						this.mergeCooldownCounter = 40;
						for (Object closeSlime : nearby) {
							((EntitySlimeMixin) closeSlime).isMerging = false;
						}
					}
				}
			} else {
				@SuppressWarnings("unchecked")
				List<EntitySlime> nearby = this.worldObj.getEntitiesWithinAABB(EntitySlime.class, this.boundingBox.expand(12,6,12));
				EntitySlime core = nearby.stream()
						.filter(s -> ((EntitySlimeExtend)s).meap$getIsCore() == (byte) 1 && s.isEntityAlive())
						.findFirst().orElse(null);
				if (this.isMerging) {
					if (core != null) {
						faceEntity(core, 10F, 20F);
					} else {
						this.isMerging = false;
					}
				}
			}
		}
	}

	@Inject(
			method = "setSlimeSize(I)V",
			at = @At(value = "TAIL")
	)
	private void onlyDropXpIfSmall(int iSize, CallbackInfo ci) {
		if (!this.isMagma) {
			if (iSize == 1) {
				this.experienceValue = 2;
			} else {
				this.experienceValue = 0;
			}
		}
	}

	@Unique
	public EntitySlime simpleCreateInstance() {
		return SlimeSpawnHelper.createChildSlime(self);
	}

	@Override
	public void meap$simpleSetDead() {
		if (!this.worldObj.isRemote) {
			this.setDead(); // proper removal for 1.6.4
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		tag.setByte("IsCore", this.meap$getIsCore());
		tag.setInteger("SlimeSize", this.getSlimeSize());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		this.meap$setIsCore(tag.getByte("IsCore"));
		this.setSlimeSize(tag.getInteger("SlimeSize"));
	}

	@Override
	public void setDead() {
		int size = this.getSlimeSize();

		if (!this.worldObj.isRemote && size > 1 && this.getHealth() <= 0)
		{
			int count = 2 + this.rand.nextInt(3);

			boolean coreNeeded = self.getSlimeSize() == (byte)4 || this.meap$getIsCore() == (byte)1;

			for (int i = 0; i < count; ++i)
			{
				float dx = ((float)(i % 2) - 0.5F) * (float)size / 40.0F;
				float dz = ((float)(i / 2) - 0.5F) * (float)size / 40.0F;
				EntitySlime child = this.simpleCreateInstance();
				SlimeSpawnHelper.applyChildSize(child, size / 2);
				if (coreNeeded) {
					((EntitySlimeExtend)child).meap$setIsCore((byte)1);
					coreNeeded = false;
				}
				child.setLocationAndAngles(this.posX + (double)dx, this.posY + 0.5D, this.posZ + (double)dz, this.rand.nextFloat() * 360.0F, 0.0F);
				this.worldObj.spawnEntityInWorld(child);
			}
		}
		this.isDead = true;
	}
}
