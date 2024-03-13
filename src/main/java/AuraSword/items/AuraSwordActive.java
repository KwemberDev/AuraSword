package AuraSword.items;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import AuraSword.CustomParticle;

import java.util.*;

import static AuraSword.AuraSwordMod.MODID;

public class AuraSwordActive extends ItemSword {
    private static final double REACH_DISTANCE = 12.0D; // Set this to your desired reach distance

    public AuraSwordActive(Item.ToolMaterial material) {
        super(material);
        setRegistryName("auraswordactive");
        setTranslationKey(MODID + ".auraswordactive");
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = stack.getTagCompound();
        // Set the lore
        NBTTagCompound display = tag.getCompoundTag("display");
        NBTTagList lore = new NBTTagList();

        lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74\u00A7l With Its Aura Unleashed, Its True From Has Been Revealed... \u00A7c\u00A7kte"));

        display.setTag("Lore", lore);
        tag.setTag("display", display);

        // make item unbreakable
        tag.setBoolean("Unbreakable", true);

        return super.initCapabilities(stack, nbt);

    }


    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        final Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            double damageValue = 1.166; // Default damage value
            double attackspeed = 1.25;
            replaceModifier(modifiers, SharedMonsterAttributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER, damageValue);
            replaceModifier(modifiers, SharedMonsterAttributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER, attackspeed);
        }

        return modifiers;
    }

    /**
     * Replace a modifier in the {@link Multimap} with a copy that's had {@code multiplier} applied to its value.
     *
     * @param modifierMultimap The MultiMap
     * @param attribute        The attribute being modified
     * @param id               The ID of the modifier
     * @param multiplier       The multiplier to apply
     */
    private void replaceModifier(Multimap<String, AttributeModifier> modifierMultimap, IAttribute attribute, UUID id, double multiplier) {
        // Get the modifiers for the specified attribute
        final Collection<AttributeModifier> modifiers = modifierMultimap.get(attribute.getName());

        // Find the modifier with the specified ID, if any
        final Optional<AttributeModifier> modifierOptional = modifiers.stream().filter(attributeModifier -> attributeModifier.getID().equals(id)).findFirst();

        if (modifierOptional.isPresent()) { // If it exists,
            final AttributeModifier modifier = modifierOptional.get();
            modifiers.remove(modifier); // Remove it
            modifiers.add(new AttributeModifier(modifier.getID(), modifier.getName(), modifier.getAmount() * multiplier, modifier.getOperation())); // Add the new modifier
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entityIn;
            if (isSelected) {
                player.interactionManager.setBlockReachDistance(REACH_DISTANCE);
                // Send packet to client to update reach distance
            } else {
                player.interactionManager.setBlockReachDistance(5.0D); // Reset to default reach distance
                // Send packet to client to update reach distance
            }
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
            NBTTagCompound nbt = itemStack.getTagCompound();
            if (!worldIn.isRemote && !playerIn.getCooldownTracker().hasCooldown(this)) {
                playerIn.getCooldownTracker().setCooldown(this, 20 * 5);

                // Spawn particles
                Vec3d lookVec = playerIn.getLookVec(); // Direction the player is looking in
                Vec3d worldUpVec = new Vec3d(0, 1, 0); // World's up vector
                Vec3d rightVec = lookVec.crossProduct(worldUpVec).normalize(); // Right vector is now based on the player's rotation
                Vec3d upVec = rightVec.crossProduct(lookVec).normalize(); // Up vector is now based on the player's rotation

                // Spawn the particles
                for (int i = 0; i < 1000; i++) {
                    // Add a small random offset to the position where the particle is spawned
                    double offsetX = (Math.random() - 0.5) * 15;
                    double offsetY = (Math.random() - 0.5);
                    double offsetZ = (Math.random() - 0.5) * 1.5;

                    // Adjust the offset vectors to be relative to the player's look vector and up vector
                    Vec3d offsetVec = lookVec.scale(offsetZ).add(rightVec.scale(offsetX)).add(upVec.scale(offsetY));

                    // Spawn the particle in the direction the player is looking
                    CustomParticle particle = new CustomParticle(worldIn, playerIn, playerIn.posX + offsetVec.x, playerIn.posY + 1.5 + offsetVec.y, playerIn.posZ + offsetVec.z);
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle);


                    offsetX = (Math.random() - 0.5);
                    offsetY = (Math.random() - 0.5) * 15;
                    offsetZ = (Math.random() - 0.5) * 1.5;

                    // Adjust the offset vectors to be relative to the player's look vector and up vector
                    Vec3d offsetVec2 = lookVec.scale(offsetZ).add(rightVec.scale(offsetX)).add(upVec.scale(offsetY));

                    // Spawn the particle in the direction the player is looking
                    CustomParticle particle2 = new CustomParticle(worldIn, playerIn, playerIn.posX + offsetVec2.x, playerIn.posY + 1.5 + offsetVec2.y, playerIn.posZ + offsetVec2.z);
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle2);

                    Random rand = new Random();
                    if (i > 750) {
                        double velocityX = (rand.nextFloat() - 0.5) / 3 + (playerIn.motionX * 2);
                        double velocityY = (rand.nextFloat() - 0.5) / 2 + Math.abs(playerIn.motionY / 1.5);
                        double velocityZ = (rand.nextFloat() - 0.5) / 3 + (playerIn.motionZ * 2);

                        worldIn.spawnParticle(EnumParticleTypes.FLAME, playerIn.posX, playerIn.posY + 1, playerIn.posZ, velocityX, velocityY, velocityZ);
                    }
                }

                String message;
                message = "\u00A7c\u00A7kte\u00A74\u00A7l CROSS-IMPACT! \u00A7c\u00A7kte";
                // Send a message to the action bar
                if (!worldIn.isRemote) {
                    playerIn.sendStatusMessage(new TextComponentString(message), true);
                    return new ActionResult<>(EnumActionResult.PASS, itemStack);
                }

                if (!worldIn.isRemote) {
                    // Damage the enemies
                    List<Entity> entities = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getEntityBoundingBox().expand(lookVec.x, lookVec.y, lookVec.z).grow(25.0D));
                    for (Entity entity : entities) {
                        if (entity instanceof IMob) { // Check if the entity is a mob
                            entity.attackEntityFrom(DamageSource.causePlayerDamage(playerIn), 20.0F); // Damage all mobs within range
                        }
                    }
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        }
}
