package AuraSword;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DamageEntity extends Entity {
    private int lifespan;
    private Vec3d velocity;

    public DamageEntity(World worldIn, Vec3d velocity) {
        super(worldIn);
        this.lifespan = 80; // Lifespan in ticks (20 ticks = 1 second)
        this.velocity = velocity;
        this.width = 0.0F;
        this.height = 0.0F;
    }
    @Override
    public void onUpdate() {
        super.onUpdate();

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
            if (entity instanceof EntityLivingBase) {
                // Apply damage to the entity
                entity.attackEntityFrom(DamageSource.GENERIC, 100.0F);
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


