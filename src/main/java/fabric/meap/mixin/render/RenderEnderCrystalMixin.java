package fabric.meap.mixin.render;

import net.minecraft.src.*;
import btw.community.abbyread.meap.extend.EntityEnderCrystalExtend;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderEnderCrystal.class)
public abstract class RenderEnderCrystalMixin extends Render {

    @Unique
    private static final ResourceLocation DRIED_CRYSTAL_TEXTURE =
            new ResourceLocation("assets/mea_textures/crystal_dried.png");

    @Unique
    private static final ResourceLocation BEAM_TEXTURE =
            new ResourceLocation("textures/entity/enderdragon/beam.png");

    @Unique
    private static final byte DRIED_STATE = 1;

    @Unique
    private static final float BEAM_RADIUS = 0.75F;

    @Unique
    private static final int BEAM_SEGMENTS = 8;

    @ModifyArgs(
            method = "doRenderEnderCrystal(Lnet/minecraft/src/EntityEnderCrystal;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderEnderCrystal;bindTexture(Lnet/minecraft/src/ResourceLocation;)V")
    )
    private void useDriedTextureIfNeeded(Args args) {
        args.set(0, DRIED_CRYSTAL_TEXTURE);
    }

    @ModifyArgs(
            method = "doRenderEnderCrystal(Lnet/minecraft/src/EntityEnderCrystal;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ModelBase;render(Lnet/minecraft/src/Entity;FFFFFF)V")
    )
    private void freezeAnimationIfDried(Args args) {
        Entity entity = args.get(0);
        if (entity instanceof EntityEnderCrystalExtend crystal) {
	        if (crystal.meap$getIsDried() == 1) { // DRIED_STATE
                args.set(2, 0.0F); // Stop rotation animation
                args.set(3, 0.0F); // Stop bobbing animation
            }
        }
    }

    @Inject(
            method = "doRenderEnderCrystal(Lnet/minecraft/src/EntityEnderCrystal;DDDFF)V",
            at = @At("TAIL")
    )
    private void addChargingBeamEffect(EntityEnderCrystal crystal, double x, double y, double z,
                                       float yaw, float partialTicks, CallbackInfo ci) {
        renderChargingBeam(crystal, x, y, z, yaw, partialTicks);
    }

    @Unique
    private boolean isDried(EntityEnderCrystal crystal) {
        return ((EntityEnderCrystalExtend) crystal).meap$getIsDried() == DRIED_STATE;
    }

    @Unique
    private EntityEnderCrystal getChargingTarget(EntityEnderCrystal crystal) {
        return ((EntityEnderCrystalExtend) crystal).meap$getChargingEnderCrystal();
    }

    @Unique
    private void renderChargingBeam(EntityEnderCrystal crystal,
                                    double renderX,
                                    double renderY,
                                    double renderZ,
                                    @SuppressWarnings("unused") float yaw,
                                    float partialTicks) {
        EntityEnderCrystal target = getChargingTarget(crystal);
        if (target == null) {
            return;
        }

        // Calculate target position with interpolation
        float targetRotation = (float) target.innerRotation + partialTicks;
        float targetBobOffset = calculateBobOffset(targetRotation);

        float deltaX = interpolatePosition(target.posX, crystal.posX, crystal.prevPosX, partialTicks);
        float deltaY = targetBobOffset + interpolatePosition(target.posY, crystal.posY, crystal.prevPosY, partialTicks);
        float deltaZ = interpolatePosition(target.posZ, crystal.posZ, crystal.prevPosZ, partialTicks);

        float horizontalDistance = MathHelper.sqrt_float(deltaX * deltaX + deltaZ * deltaZ);
        float totalDistance = MathHelper.sqrt_float(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        // Set up rendering transformation
        GL11.glPushMatrix();
        GL11.glTranslatef((float) renderX, (float) renderY + 1.0F, (float) renderZ);
        GL11.glRotatef(calculateYawRotation(deltaZ, deltaX), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(calculatePitchRotation(horizontalDistance, deltaY), 1.0F, 0.0F, 0.0F);

        // Render the beam
        setupBeamRendering();
        bindTexture(BEAM_TEXTURE);
        drawBeamGeometry(crystal, partialTicks, deltaX, deltaY, deltaZ, totalDistance);
        cleanupBeamRendering();

        GL11.glPopMatrix();
    }

    @Unique
    private float calculateBobOffset(float rotation) {
        float bob = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;
        return (bob * bob + bob) * 0.2F;
    }

    @Unique
    private float interpolatePosition(double current, double renderPos, double prevPos, float partialTicks) {
        return (float) (current - renderPos - (prevPos - renderPos) * (1.0F - partialTicks));
    }

    @Unique
    private float calculateYawRotation(float deltaZ, float deltaX) {
        return (float) (-Math.atan2(deltaZ, deltaX)) * 180.0F / (float) Math.PI - 90.0F;
    }

    @Unique
    private float calculatePitchRotation(float horizontalDist, float deltaY) {
        return (float) (-Math.atan2(horizontalDist, deltaY)) * 180.0F / (float) Math.PI - 90.0F;
    }

    @Unique
    private void setupBeamRendering() {
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
    }

    @Unique
    private void cleanupBeamRendering() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_FLAT);
        RenderHelper.enableStandardItemLighting();
    }

    @Unique
    private void drawBeamGeometry(EntityEnderCrystal crystal, float partialTicks,
                                  @SuppressWarnings("unused") float deltaX,
                                  @SuppressWarnings("unused") float deltaY,
                                  @SuppressWarnings("unused") float deltaZ,
                                  float distance) {
        Tessellator tessellator = Tessellator.instance;

        float textureOffsetStart = calculateTextureOffset(crystal, partialTicks, 0.0F);
        float textureOffsetEnd = calculateTextureOffset(crystal, partialTicks, distance);

        tessellator.startDrawing(5); // GL_TRIANGLE_STRIP

        for (int segment = 0; segment <= BEAM_SEGMENTS; segment++) {
            float angle = (float) (segment % BEAM_SEGMENTS) * (float) Math.PI * 2.0F / (float) BEAM_SEGMENTS;
            float offsetX = MathHelper.sin(angle) * BEAM_RADIUS;
            float offsetY = MathHelper.cos(angle) * BEAM_RADIUS;
            float textureU = (float) (segment % BEAM_SEGMENTS) / (float) BEAM_SEGMENTS;

            // Inner vertex (dark)
            tessellator.setColorOpaque_I(0);
            tessellator.addVertexWithUV(offsetX * 0.2F, offsetY * 0.2F, 0.0D, textureU, textureOffsetEnd);

            // Outer vertex (white)
            tessellator.setColorOpaque_I(16777215);
            tessellator.addVertexWithUV(offsetX, offsetY, distance, textureU, textureOffsetStart);
        }

        tessellator.draw();
    }

    @Unique
    private float calculateTextureOffset(EntityEnderCrystal crystal, float partialTicks, float distanceModifier) {
        float baseDistance = distanceModifier == 0.0F ? 0.0F : distanceModifier / 32.0F;
        return baseDistance - ((float) crystal.ticksExisted + partialTicks) * 0.01F;
    }
}