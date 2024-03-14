package AuraSword;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PacketParticle implements IMessage {
    private List<ParticleData> particles;

    public PacketParticle() {
        this.particles = new ArrayList<>();
    }

    public PacketParticle(List<CustomParticle> particles) {
        this.particles = particles.stream()
                .map(p -> new ParticleData(p.posX(), p.posY(), p.posZ(), p.getSpeedX(), p.getSpeedY(), p.getSpeedZ(), p.getUUID())) // Modify this line
                .collect(Collectors.toList());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(particles.size());
        for (ParticleData particle : particles) {
            buf.writeDouble(particle.x);
            buf.writeDouble(particle.y);
            buf.writeDouble(particle.z);
            buf.writeDouble(particle.speedX);
            buf.writeDouble(particle.speedY);
            buf.writeDouble(particle.speedZ);
            PacketBuffer packetBuffer = new PacketBuffer(buf);
            packetBuffer.writeUniqueId(particle.sourceEntityUUID);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            double speedX = buf.readDouble();
            double speedY = buf.readDouble();
            double speedZ = buf.readDouble();
            PacketBuffer packetBuffer = new PacketBuffer(buf);
            UUID sourceEntityUUID = packetBuffer.readUniqueId();
            particles.add(new ParticleData(x, y, z, speedX, speedY, speedZ, sourceEntityUUID));

        }
    }

    public List<ParticleData> getParticles() {
        return particles;
    }

    public static class Handler implements IMessageHandler<PacketParticle, IMessage> {
        @Override
        public IMessage onMessage(PacketParticle message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;

                for (ParticleData particle : message.getParticles()) {
                    EntityPlayer sourceEntity = world.getPlayerEntityByUUID(particle.sourceEntityUUID);
                    if (sourceEntity != null) {
                        CustomParticle newParticle = new CustomParticle(world, sourceEntity, particle.x, particle.y, particle.z, particle.speedX, particle.speedY, particle.speedZ); // Modify this line
                        Minecraft.getMinecraft().effectRenderer.addEffect(newParticle);
                    }
                }
            });
            return null;
        }
    }


    public static class ParticleData {
        public double x, y, z;
        public double speedX, speedY, speedZ;
        public UUID sourceEntityUUID; // Add this line

        public ParticleData(double x, double y, double z, double speedX, double speedY, double speedZ, UUID sourceEntityUUID) { // Modify this line
            this.x = x;
            this.y = y;
            this.z = z;
            this.speedX = speedX;
            this.speedY = speedY;
            this.speedZ = speedZ;
            this.sourceEntityUUID = sourceEntityUUID; // Add this line
        }
    }
}

