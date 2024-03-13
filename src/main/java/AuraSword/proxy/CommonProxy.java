package AuraSword.proxy;

import AuraSword.items.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.*;
import AuraSword.CustomRecipe;

import static AuraSword.AuraSwordMod.MODID;

@Mod.EventBusSubscriber
public class CommonProxy {
    public static Configuration config;
    public void preInit(FMLPreInitializationEvent e) {
    }

    public void init(FMLInitializationEvent e) {
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    // IMPORTANT
    // Here in commonproxy is where you register the blocks and items. by adding them to the registry.
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new AuraSword(Item.ToolMaterial.DIAMOND));
        event.getRegistry().register(new Sheath());
        event.getRegistry().register(new AuraSwordDefault(Item.ToolMaterial.DIAMOND));
        event.getRegistry().register(new AuraSwordSheathed(Item.ToolMaterial.DIAMOND));
        event.getRegistry().register(new AuraSwordActive(Item.ToolMaterial.DIAMOND));
    }

    @SubscribeEvent
    public static void onRegisterRecipesEvent(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().registerAll(
                new CustomRecipe().setRegistryName(new ResourceLocation(MODID, "auraswordsheathed"))
        );
    }
}