//package AuraSword;
//
//import AuraSword.items.AuraSwordDefault;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
//
//public class CraftingEventHandler {
//    @SubscribeEvent
//    public void onItemCrafted(ItemCraftedEvent event) {
//        ItemStack item = event.crafting;
//        if (item.getItem() == ModItems.auraSwordDefault) {
//            NBTTagCompound tag = item.hasTagCompound() ? item.getTagCompound() : new NBTTagCompound();
//            // Modify the tag as needed
//            item.setTagCompound(tag);
//        }
//    }
//}