package AuraSword.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DamageEntityCrossImpact extends Entity {
    private int lifespan;
    private Vec3d velocity;
    private Entity sourceentity;

    public DamageEntityCrossImpact(World worldIn, Vec3d velocity, Entity sourceentity) {
        super(worldIn);
        this.lifespan = 55; // Lifespan in ticks (20 ticks = 1 second)
        this.velocity = velocity;
        this.width = 0.0F;
        this.height = 0.0F;
        this.sourceentity = sourceentity;
    }
    @Override
    public void onUpdate() {
        super.onUpdate();
        float blockRange = 1.1F;
        float explosionrange = 4F;

        // Move the entity
        this.motionX = velocity.x;
        this.motionY = velocity.y;
        this.motionZ = velocity.z;

        // Update the entity's position
        this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        // Decrease lifespan each tick
        lifespan--;

        // If lifespan is over, remove this entity
        if (lifespan <= 0) {
            this.setDead();
        }

        // Check for entities within a certain radius

        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(15.0D, 15.0D, 15.0D).offset(-7.5D, -7.5D, -7.5D));

        for (Entity entity : list) {
            if (entity instanceof EntityLivingBase && entity != sourceentity) {
                // Apply damage to the entity
                entity.attackEntityFrom(DamageSource.GENERIC, 50.0F);
            }
        }
        Minecraft.getMinecraft().addScheduledTask(() -> {
            for (float dx = -blockRange; dx <= blockRange; dx++) {
                for (float dy = -blockRange; dy <= blockRange; dy++) {
                    for (float dz = -blockRange; dz <= blockRange; dz++) {
                        BlockPos pos = new BlockPos(this.posX + dx, this.posY + dy, this.posZ + dz);
                        BlockPos posAbove = pos.up();
                        IBlockState state = this.world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (!this.world.isAirBlock(posAbove) && block != Blocks.WATER && block != Blocks.LAVA && !block.isPassable(world, pos)) {
                            this.world.createExplosion(sourceentity, this.posX, this.posY, this.posZ, (float) 0, true);
                            this.setDead();
                        }
                    }
                }
            }
        });

        if (lifespan % 2 == 0) {
            for (float dx = -explosionrange; dx <= explosionrange; dx++) {
                for (float dy = -explosionrange; dy <= explosionrange; dy++) {
                    for (float dz = -explosionrange; dz <= explosionrange; dz++) {
                        BlockPos pos = new BlockPos(this.posX + dx, this.posY + dy, this.posZ + dz);
                        BlockPos posAbove = pos.up(); // Get the block position above the current block
                        if (!this.world.isAirBlock(pos) && this.world.isAirBlock(posAbove)) {
                            double random = Math.random();
                            // Create an explosion at the block's position
                            if (random > 0.97D) {
                                world.createExplosion(sourceentity, pos.getX(), pos.getY() + 0.7, pos.getZ(), (float) 0, true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public float getBrightness() {
        return 0.0F;
    }

}


