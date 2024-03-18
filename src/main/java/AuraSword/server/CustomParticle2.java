package AuraSword.server;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CustomParticle2 extends Particle {
    public boolean slash;
    private final EnumParticleTypes type;
    public UUID sourceEntityUUID;
    private final EntityLivingBase sourceEntity;
    public static List<CustomParticle2> activeParticles = new ArrayList<>();
    public CustomParticle2(World worldIn, EntityPlayer playerIn, double posXIn, double posYIn, double posZIn, double speedX, double speedY, double speedZ, EnumParticleTypes type, boolean isSlash) {
        super(worldIn, posXIn, posYIn, posZIn, 300, 300, 300);
        this.type = type;
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("aurasword:particle/beam2"));
        this.particleScale = 40.0F; // Change this to the desired scale
        this.particleMaxAge = 100; // set the max lifespan of the particle to 5 seconds (100 ticks)
        this.sourceEntity = playerIn;
        // Get the player's look vector

        // Set the particle's velocity to the look vector scaled by the desired speed
        this.motionX = speedX;
        this.motionY = speedY;
        this.motionZ = speedZ;
        activeParticles.add(this);// Add this line
        this.slash = isSlash;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            activeParticles.remove(this);
        }
        if (this.particleAge % 4 == 0) {

            Random rand = new Random();
            double radius = 2;
            if (!slash) {
                radius = 5; // size of trail cloud
            }
            for (int i = 0; i < 25; i++) {
                double offsetX = (rand.nextDouble() - 0.5) * 2 * radius;
                double offsetY = (rand.nextDouble() - 0.5) * 2 * radius;
                double offsetZ = (rand.nextDouble() - 0.5) * 2 * radius;
                this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + offsetX, this.posY + offsetY, this.posZ + offsetZ, 0.0D, 0.0D, 0.0D);
                if (Math.random() > 0.95D) {
                    this.world.createExplosion(sourceEntity, this.posX + offsetX, this.posY + offsetY, this.posZ + offsetZ, (float) 0, true);
                }
            }

            float blockRange = 1.1F;
            double entityRange = 3.0;
            List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(
                    this.posX - entityRange, this.posY - entityRange, this.posZ - entityRange,
                    this.posX + entityRange, this.posY + entityRange, this.posZ + entityRange));
            for (Entity entity : entities) {
                if (entity instanceof EntityLivingBase && entity != sourceEntity) {
                    this.world.createExplosion(sourceEntity, this.posX, this.posY, this.posZ, (float) 0, true);
                }
            }

            for (float dx = -blockRange; dx <= blockRange; dx++) {
                for (float dy = -blockRange; dy <= blockRange; dy++) {
                    for (float dz = -blockRange; dz <= blockRange; dz++) {
                        BlockPos pos = new BlockPos(this.posX + dx, this.posY + dy, this.posZ + dz);
                        BlockPos posAbove = pos.up(); // Get the block position above the current block
                        BlockPos posAboveAbove = posAbove.up();
                        IBlockState state = this.world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (!this.world.isAirBlock(pos) && !this.world.isAirBlock(posAboveAbove) && block != Blocks.WATER && block != Blocks.LAVA && !block.isPassable(world, pos)) {
                            this.setExpired();
                        }
                    }
                }
            }
            entities.clear();
        }
    }

    @Override
    public void move(double x, double y, double z) {
        // Create a new bounding box that is offset by the given x, y, and z values
        AxisAlignedBB newBoundingBox = this.getBoundingBox().offset(x, y, z);

        // Set the particle's bounding box to the new bounding box
        this.setBoundingBox(newBoundingBox);

        // Update the particle's position to match the new bounding box
        this.posX = (newBoundingBox.minX + newBoundingBox.maxX) / 2.0D;
        this.posY = newBoundingBox.minY;
        this.posZ = (newBoundingBox.minZ + newBoundingBox.maxZ) / 2.0D;
    }



    @Override
    public int getFXLayer() {
        return 1; //
    }

    public double posX() {
        return this.posX;
    }
    public double posY() {
        return this.posY;
    }
    public double posZ() {
        return this.posZ;
    }

    public double getSpeedY() {
        return this.motionY;
    }
    public double getSpeedX() {
        return this.motionX;
    }
    public double getSpeedZ() {
        return this.motionZ;
    }
    public boolean getSlash() {
        return this.slash;
    }
    public UUID getUUID() {
        return this.sourceEntityUUID;
    }


    public EnumParticleTypes getParticleType() {
        return this.type;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = (float)this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float)this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            f = this.particleTexture.getMinU();
            f1 = this.particleTexture.getMaxU();
            f2 = this.particleTexture.getMinV();
            f3 = this.particleTexture.getMaxV();
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3d[] avec3d = new Vec3d[] {new Vec3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};

        if (this.particleAngle != 0.0F)
        {
            float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = MathHelper.cos(f8 * 0.5F);
            float f10 = MathHelper.sin(f8 * 0.5F);
            float f11 = MathHelper.sin(f8 * 0.5F);
            float f12 = MathHelper.sin(f8 * 0.5F);
            Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

            for (int l = 0; l < 4; ++l)
            {
                avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
            }
        }

        buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        // Return a high value for fullbright
        return 0xf000f0;
    }
}