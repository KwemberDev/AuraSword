package AuraSword;

import AuraSword.items.AuraSwordDefault;
import AuraSword.items.Sheath;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SheathedSwordRecipe implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundSword = false;
        boolean foundSheath = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof AuraSwordDefault) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag != null && tag.getBoolean("Unbreakable")) {
                        foundSword = true;
                    }
                } else if (stack.getItem() instanceof Sheath) {
                    foundSheath = true;
                }
            }
        }

        return foundSword && foundSheath;
    }


    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = new ItemStack(ModItems.auraSwordActive);
        NBTTagCompound tag = result.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            result.setTagCompound(tag);
        }
        tag.setBoolean("Unbreakable", true);
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ModItems.auraSwordActive);
    }

    @Override
    public IRecipe setRegistryName(ResourceLocation name) {
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation("aurasword", "auraswordactive");
    }

    @Override
    public Class<IRecipe> getRegistryType() {
        return IRecipe.class;
    }
}
