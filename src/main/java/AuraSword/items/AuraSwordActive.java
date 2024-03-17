package AuraSword.items;

import AuraSword.client.DamageEntityCrossImpact;
import AuraSword.client.DamageEntitySlash;
import AuraSword.proxy.CommonProxy;
import AuraSword.server.CustomParticle;
import AuraSword.server.CustomParticle2;
import AuraSword.server.PacketParticle;
import AuraSword.server.PacketParticle2;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static AuraSword.AuraSwordMod.MODID;
import static AuraSword.proxy.CommonProxy.AURA;
import static AuraSword.proxy.CommonProxy.AURASHORT;

public class AuraSwordActive extends ItemSword {
    int tickcount = 0;
    int duracount = 0;
    public static final String TEXTURE_KEY = "texture";
    public boolean didattack = false;

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

        lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74\u00A7l Sheathed in the Roots of the World Tree \u00A7c\u00A7kte"));
        lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74\u00A7l This Sword Continuously Absorbs Aura.... \u00A7c\u00A7kte"));
        lore.appendTag(new NBTTagString(""));
        lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74 Sneak + Right-Click to \u00A7lActivate Aura! \u00A7c\u00A7kte"));

        display.setTag("Lore", lore);
        tag.setTag("display", display);

        return super.initCapabilities(stack, nbt);

    }


    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        final Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            double damageValue = 1.166; // Default damage value
            double attackspeed = 1.25;
            if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(TEXTURE_KEY)) {
                damageValue = 2.0; // Damage value when the texture is active
                attackspeed = 1;
            }
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
        ModelLoader.setCustomMeshDefinition(this, stack -> {
            if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(TEXTURE_KEY)) {
                return new ModelResourceLocation("aurasword:auraswordactive", "inventory");
            } else {
                return new ModelResourceLocation("aurasword:auraswordsheathed", "inventory");
            }
        });
        ModelBakery.registerItemVariants(this,
                new ModelResourceLocation("aurasword:auraswordsheathed", "inventory"),
                new ModelResourceLocation("aurasword:auraswordactive", "inventory"));

    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.getBoolean(TEXTURE_KEY)) {
            return "\u00A7cAwakened Gigant"; // Name for the active texture version
        } else {
            return "\u00A7cGigant"; // Name for the sheathed texture version
        }
    }




    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer playerIn = (EntityPlayer) entityIn;
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null && nbt.getBoolean(TEXTURE_KEY)) {
                // Check if the player has the custom effect
                if (!playerIn.isPotionActive(AURA)) {
                    nbt.setBoolean(TEXTURE_KEY, false);
                    playerIn.addPotionEffect(new PotionEffect(AURASHORT, 600));
                }
            }
        }
    }



    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) { // Player MUST be sneaking
            itemStack = playerIn.getHeldItem(handIn);

            if (!itemStack.hasTagCompound()) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound nbt = itemStack.getTagCompound();
            boolean texture = nbt.getBoolean(TEXTURE_KEY);

            if (!playerIn.getCooldownTracker().hasCooldown(this) && !playerIn.isPotionActive(AURASHORT) && !playerIn.isPotionActive(AURA)) {
                playerIn.getCooldownTracker().setCooldown(this, 20);
                if (!playerIn.isPotionActive(AURA)) {
                    playerIn.addPotionEffect(new PotionEffect(AURA, 600));
                }
                nbt.setBoolean(TEXTURE_KEY, !texture);

                // Change the lore
                NBTTagCompound display = nbt.getCompoundTag("display");
                NBTTagList lore = new NBTTagList();

                String message;
                if (texture) {
                    lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74\u00A7l Sheathed in the Roots of the World Tree \u00A7c\u00A7kte"));
                    lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74\u00A7l This Sword Continuously Absorbs Aura.... \u00A7c\u00A7kte"));
                    lore.appendTag(new NBTTagString(""));
                    lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74 Sneak + Right-Click to \u00A7lActivate Aura! \u00A7c\u00A7kte"));
                    message = "\u00A7c\u00A7kte\u00A74\u00A7l Aura Not Active \u00A7c\u00A7kte";
                } else {
                    lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74\u00A7l With Its Aura Unleashed, Its True From Has Been Revealed... \u00A7c\u00A7kte"));
                    lore.appendTag(new NBTTagString(""));
                    lore.appendTag(new NBTTagString("\u00A7c\u00A7kte\u00A74 Sneak + Right-Click to \u00A7lDeactivate Aura! \u00A7c\u00A7kte"));
                    message = "\u00A7c\u00A7kte\u00A74\u00A7l Aura Activated \u00A7c\u00A7kte";
                }

                display.setTag("Lore", lore);
                nbt.setTag("display", display);

                // Send a message to the action bar
                if (!worldIn.isRemote) {
                    playerIn.sendStatusMessage(new TextComponentString(message), true);
                    return new ActionResult<>(EnumActionResult.PASS, itemStack);
                }

                Random rand = new Random();
                // The parameters are: particle type, x, y, z, x speed, y speed, z speed
                if (!texture) {
                    for (int i = 0; i < 500; i++) { // number of particles andrandom velocity generator between -0.5 and 0.5 for each axis
                        double velocityX = (rand.nextFloat() - 0.5) / 3 + playerIn.motionX;
                        double velocityY = (rand.nextFloat() - 0.5) / 2 + Math.abs(playerIn.motionY / 1.5);
                        double velocityZ = (rand.nextFloat() - 0.5) / 3 + playerIn.motionZ;

                        worldIn.spawnParticle(EnumParticleTypes.FLAME, playerIn.posX, playerIn.posY + 1, playerIn.posZ, velocityX, velocityY, velocityZ);
                    }
                }

                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
        }
        else {
            NBTTagCompound nbt = itemStack.getTagCompound();
            boolean texture = nbt.getBoolean(TEXTURE_KEY);
            if (!playerIn.getCooldownTracker().hasCooldown(this) && texture) {
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));

            }
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, itemStack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer playerIn = (EntityPlayer) entityLiving;
            int chargeTime = this.getMaxItemUseDuration(stack) - timeLeft;

            // item charged over 1 second?
            if (chargeTime >= 30) { // 20 ticks = 1 second
                Random rand = new Random();
                // The parameters are: particle type, x, y, z, x speed, y speed, z speed
                for (int i = 0; i < 100; i++) { // number of particles andrandom velocity generator between -0.5 and 0.5 for each axis
                    double velocityX = (rand.nextFloat() - 0.5) / 3 + playerIn.motionX;
                    double velocityY = (rand.nextFloat() - 0.5) / 2 + Math.abs(playerIn.motionY / 1.5);
                    double velocityZ = (rand.nextFloat() - 0.5) / 3 + playerIn.motionZ;

                    worldIn.spawnParticle(EnumParticleTypes.FLAME, playerIn.posX, playerIn.posY + 1, playerIn.posZ, velocityX, velocityY, velocityZ);
                }



                Vec3d lookVec = playerIn.getLookVec(); // Direction the player is looking in
                Vec3d worldUpVec = new Vec3d(0, 1, 0); // World's up vector
                Vec3d rightVec = lookVec.crossProduct(worldUpVec).normalize(); // Right vector is based on the player's rotation
                Vec3d upVec = rightVec.crossProduct(lookVec).normalize(); // Up vector is  based on the player's rotation

                // Spawn the particles
                List<CustomParticle> particles = new ArrayList<>();
                List<CustomParticle2> particles2 = new ArrayList<>();
                for (int i = 0; i < 1; i++) {
                    Vec3d offsetVec;

                    // Add a small random offset to the position where the particle is spawned
                    double offsetX = 0;
                    double offsetY = 0;
                    double offsetZ = 0;

                    // Adjust the offset vectors to be relative to the player's look vector and up vector
                    offsetVec = lookVec.scale(offsetZ).add(rightVec.scale(offsetX)).add(upVec.scale(offsetY));

                    // Spawn the particle in the direction the player is looking
                    CustomParticle particle = new CustomParticle(worldIn, playerIn, playerIn.posX + offsetVec.x, playerIn.posY + 1.5 + offsetVec.y, playerIn.posZ + offsetVec.z, lookVec.x * 3, lookVec.y * 3, lookVec.z * 3, EnumParticleTypes.FLAME);
                    particles.add(particle);

                    offsetX = 0;
                    offsetY = 0.3;
                    offsetZ = 0;

                    // Adjust the offset vectors to be relative to the player's look vector and up vector
                    Vec3d offsetVec2 = lookVec.scale(offsetZ).add(rightVec.scale(offsetX)).add(upVec.scale(offsetY));

                    // Spawn the particle in the direction the player is looking
                    CustomParticle2 particle2 = new CustomParticle2(worldIn, playerIn, playerIn.posX + offsetVec2.x, playerIn.posY + 1.5 + offsetVec2.y, playerIn.posZ + offsetVec2.z,  lookVec.x * 3, lookVec.y * 3, lookVec.z * 3, EnumParticleTypes.FLAME, false);
                    particles2.add(particle2);
                }
                PacketParticle packet = new PacketParticle(particles);
                PacketParticle2 packet2 = new PacketParticle2(particles2);
                DamageEntityCrossImpact damageEntity = new DamageEntityCrossImpact(worldIn, lookVec.scale(2.0), playerIn);
                damageEntity.setPosition(playerIn.posX, playerIn.posY + 1.5, playerIn.posZ);
                worldIn.spawnEntity(damageEntity);
                if (!worldIn.isRemote && FMLCommonHandler.instance() != null && FMLCommonHandler.instance().getMinecraftServerInstance() != null && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList() != null && CommonProxy.network != null) {
                    for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                        CommonProxy.network.sendTo(packet, player);
                        CommonProxy.network.sendTo(packet2, player);
                    }
                }
                String message;
                message = "\u00A7c\u00A7kte\u00A74\u00A7l CROSS-IMPACT! \u00A7c\u00A7kte";
                // Send a message to the action bar
                if (!worldIn.isRemote) {
                    playerIn.sendStatusMessage(new TextComponentString(message), true);
                    new ActionResult<>(EnumActionResult.PASS, stack);
                }
                playerIn.getCooldownTracker().setCooldown(this, 30);
            }
            if (chargeTime < 30) { // 20 ticks = 1 second
                Random rand = new Random();
                // The parameters are: particle type, x, y, z, x speed, y speed, z speed
                for (int i = 0; i < 100; i++) { // number of particles andrandom velocity generator between -0.5 and 0.5 for each axis
                    double velocityX = (rand.nextFloat() - 0.5) / 3 + playerIn.motionX;
                    double velocityY = (rand.nextFloat() - 0.5) / 2 + Math.abs(playerIn.motionY / 1.5);
                    double velocityZ = (rand.nextFloat() - 0.5) / 3 + playerIn.motionZ;

                    worldIn.spawnParticle(EnumParticleTypes.FLAME, playerIn.posX, playerIn.posY + 1, playerIn.posZ, velocityX, velocityY, velocityZ);
                }



                Vec3d lookVec = playerIn.getLookVec(); // Direction the player is looking in
                Vec3d worldUpVec = new Vec3d(0, 1, 0); // World's up vector
                Vec3d rightVec = lookVec.crossProduct(worldUpVec).normalize(); // Right vector is based on the player's rotation
                Vec3d upVec = rightVec.crossProduct(lookVec).normalize(); // Up vector is  based on the player's rotation

                // Spawn the particles
                List<CustomParticle2> particles = new ArrayList<>();
                for (int i = 0; i < 1; i++) {
                    Vec3d offsetVec;

                    // Add a small random offset to the position where the particle is spawned
                    double offsetX = 0;
                    double offsetY = 0;
                    double offsetZ = 0;

                    // Adjust the offset vectors to be relative to the player's look vector and up vector
                    offsetVec = lookVec.scale(offsetZ).add(rightVec.scale(offsetX)).add(upVec.scale(offsetY));

                    // Spawn the particle in the direction the player is looking
                    CustomParticle2 particle = new CustomParticle2(worldIn, playerIn, playerIn.posX + offsetVec.x, playerIn.posY + 1.5 + offsetVec.y, playerIn.posZ + offsetVec.z, lookVec.x * 3, lookVec.y * 3, lookVec.z * 3, EnumParticleTypes.FLAME, true);
                    particles.add(particle);
                }
                PacketParticle2 packet = new PacketParticle2(particles);
                DamageEntitySlash damageEntity = new DamageEntitySlash(worldIn, lookVec.scale(2.0), playerIn);
                damageEntity.setPosition(playerIn.posX, playerIn.posY + 1.5, playerIn.posZ);
                worldIn.spawnEntity(damageEntity);
                if (!worldIn.isRemote && FMLCommonHandler.instance() != null && FMLCommonHandler.instance().getMinecraftServerInstance() != null && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList() != null && CommonProxy.network != null) {
                    for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                        CommonProxy.network.sendTo(packet, player);
                    }
                }
                String message;
                message = "\u00A7c\u00A7kte\u00A74\u00A7l AURA SLASH! \u00A7c\u00A7kte";
                // Send a message to the action bar
                if (!worldIn.isRemote) {
                    playerIn.sendStatusMessage(new TextComponentString(message), true);
                    new ActionResult<>(EnumActionResult.PASS, stack);
                }
                playerIn.getCooldownTracker().setCooldown(this, 30);
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000; // This is the same use duration as the bow and trident
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

}