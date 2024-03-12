package AuraSword;

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

public class CustomParticle extends ParticleFlame {
    private final EntityLivingBase sourceEntity;
    public static List<CustomParticle> activeParticles = new ArrayList<>();
    public CustomParticle(World worldIn, EntityPlayer playerIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn, posXIn, posYIn, posZIn, 300, 300, 300);
        this.particleMaxAge = 100; // This will set the lifespan of the particle to 5 seconds (100 ticks)
        this.sourceEntity = playerIn;

        // Get the player's look vector
        Vec3d lookVec = playerIn.getLookVec();

        // Set the particle's velocity to the look vector scaled by the desired speed
        double speed = 2.5; // Change this to your desired speed
        this.motionX = lookVec.x * speed;
        this.motionY = lookVec.y * speed;
        this.motionZ = lookVec.z * speed;
        activeParticles.add(this);
    }

    // Override this method to change the behavior of your particle
    @Override
    public void onUpdate() {
        super.onUpdate();
        // If the particle is older than its maximum age, it will be removed
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            activeParticles.remove(this);
        }
        // Only execute the rest of the code every few ticks
        if (this.particleAge % 5 == 0) { // Change this to control how often the code is executed
            // Define the range within which to check for blocks and entities
            int blockRange = 2;
            double entityRange = 5.0;
            // Define the amount of damage to apply
            float damageAmount = 50.0F; // Change this to your desired damage amount
                // Check for entities within the defined range
                List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(
                        this.posX - entityRange, this.posY - entityRange, this.posZ - entityRange,
                        this.posX + entityRange, this.posY + entityRange, this.posZ + entityRange));

                for (Entity entity : entities) {
                    // Apply damage to the entity
                    entity.attackEntityFrom(DamageSource.causeMobDamage(this.sourceEntity), damageAmount);
                }

            // Schedule a task to be executed in the next game tick
            Minecraft.getMinecraft().addScheduledTask(() -> {
                // Check for blocks within the defined range
                for (int dx = -blockRange; dx <= blockRange; dx++) {
                    for (int dy = -blockRange; dy <= blockRange; dy++) {
                        for (int dz = -blockRange; dz <= blockRange; dz++) {
                            BlockPos pos = new BlockPos(this.posX + dx, this.posY + dy, this.posZ + dz);
                            IBlockState state = this.world.getBlockState(pos);
                            Block block = state.getBlock();

                            // If the block is not air, not bedrock, and is at or near the surface, set it to air
                            if (!this.world.isAirBlock(pos) && block.getBlockHardness(state, world, pos) > 0.0F && block != Blocks.OBSIDIAN) {
                                this.world.setBlockToAir(pos);
                                // Destroy this particle
                                this.setExpired();
                                // Destroy other particles within a certain range
                                double particleRange = 5.0; // Change this to your desired range
                                List<Entity> particles = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(
                                        this.posX - particleRange, this.posY - particleRange, this.posZ - particleRange,
                                        this.posX + particleRange, this.posY + particleRange, this.posZ + particleRange));

                                for (CustomParticle particle : CustomParticle.activeParticles) {
                                    double dx2 = particle.posX - this.posX;
                                    double dy2 = particle.posY - this.posY;
                                    double dz2 = particle.posZ - this.posZ;
                                    double distanceSquared = (dx2 * dx2) + (dy2 * dy2) + (dz2 * dz2);
                                    if (distanceSquared <= particleRange) {
                                        particle.setExpired();
                                    }
                                }
                                return; // Exit the loop after destroying a block
                            }
                        }
                    }
                }
            });
        }
    }
    @Override
    public int getFXLayer() {
        return 0; // For particles with the flame texture, this should be 0
    }
}
