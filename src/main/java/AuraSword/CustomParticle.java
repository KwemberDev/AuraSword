package AuraSword;

import ibxm.Player;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomParticle extends ParticleFlame {
    public UUID sourceEntityUUID;
    private final EntityLivingBase sourceEntity;
    public static List<CustomParticle> activeParticles = new ArrayList<>();
    public CustomParticle(World worldIn, EntityPlayer playerIn, double posXIn, double posYIn, double posZIn, double speedX, double speedY, double speedZ) {
        super(worldIn, posXIn, posYIn, posZIn, 300, 300, 300);
        this.particleMaxAge = 100; // This will set the lifespan of the particle to 5 seconds (100 ticks)
        this.sourceEntity = playerIn;
        // Get the player's look vector

        // Set the particle's velocity to the look vector scaled by the desired speed
        this.motionX = speedX;
        this.motionY = speedY;
        this.motionZ = speedZ;
        activeParticles.add(this);
        this.sourceEntityUUID = sourceEntityUUID; // Add this line
    }

    // Override this method to change the behavior of your particle
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            activeParticles.remove(this);
        }
        if (this.particleAge % 4 == 0) {
            float blockRange = 0.1F;
            double entityRange = 3.0;
            float damageAmount = 50.0F;
            List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(
                    this.posX - entityRange, this.posY - entityRange, this.posZ - entityRange,
                    this.posX + entityRange, this.posY + entityRange, this.posZ + entityRange));

            for (Entity entity : entities) {
                if (entity instanceof EntityLivingBase && entity != sourceEntity) {
                    entity.attackEntityFrom(DamageSource.causeMobDamage(this.sourceEntity), damageAmount);
                    this.world.createExplosion(sourceEntity, this.posX, this.posY, this.posZ, (float) 1/10, true);
                }
            }

            Minecraft.getMinecraft().addScheduledTask(() -> {
                for (float dx = -blockRange; dx <= blockRange; dx++) {
                    for (float dy = -blockRange; dy <= blockRange; dy++) {
                        for (float dz = -blockRange; dz <= blockRange; dz++) {
                            BlockPos pos = new BlockPos(this.posX + dx, this.posY + dy, this.posZ + dz);
                            IBlockState state = this.world.getBlockState(pos);
                            Block block = state.getBlock();

                            if (!this.world.isAirBlock(pos) && block.getBlockHardness(state, world, pos) > 0.0F && block != Blocks.OBSIDIAN) {
                                this.world.createExplosion(sourceEntity, this.posX, this.posY, this.posZ, (float) 1/10, true);
                                this.setExpired();
                            }
                        }
                    }
                }
            });
            entities.clear();
        }
    }


    @Override
    public int getFXLayer() {
        return 0; // For particles with the flame texture, this should be 0
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
    public UUID getUUID() {
        return this.sourceEntityUUID;
    }
}
