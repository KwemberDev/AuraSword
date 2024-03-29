package AuraSword.proxy;

import AuraSword.config.Config;
import AuraSword.potioneffect.Aura;
import AuraSword.potioneffect.AuraShortage;
import AuraSword.server.PacketParticle2;
import AuraSword.items.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.*;
import AuraSword.SheathedSwordRecipe;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import AuraSword.server.PacketParticle;

import java.io.File;

import static AuraSword.AuraSwordMod.MODID;

@Mod.EventBusSubscriber
public abstract class CommonProxy {
    // aurashatter sound for falling out of fighting spirit buff
    public static SoundEvent aurashatter;
    public static Potion AURA;
    public static Potion AURASHORT;
    public static Configuration config;
    public static SimpleNetworkWrapper network;
    public void preInit(FMLPreInitializationEvent e) {
        File directory = e.getModConfigurationDirectory();
        config = new net.minecraftforge.common.config.Configuration(new File(directory.getPath(), "auraswordmod.cfg"));
        Config.readConfig();

    }

    public void init(FMLInitializationEvent e) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("aurasword");
        network.registerMessage(PacketParticle.Handler.class, PacketParticle.class, 0, Side.CLIENT);
        network.registerMessage(PacketParticle2.Handler.class, PacketParticle2.class, 1, Side.CLIENT);
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (config.hasChanged()) {
            config.save();
        }
    }

    // IMPORTANT
    // Here in commonproxy is where you register the blocks and items. by adding them to the registry.
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new Sheath());
        event.getRegistry().register(new AuraSwordDefault(Item.ToolMaterial.DIAMOND, 2000));
        event.getRegistry().register(new AuraSwordActive(Item.ToolMaterial.DIAMOND, 3000));
        event.getRegistry().register(new Roots());
    }

    @SubscribeEvent
    public static void onRegisterRecipesEvent(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().registerAll(
                new SheathedSwordRecipe().setRegistryName(new ResourceLocation(MODID, "auraswordactive"))
        );
    }

    @SubscribeEvent
    public abstract void registerItemRenderer(Item item, int meta, String name);

    @SubscribeEvent
    public static void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        AURA = new Aura(false, 3694022).setRegistryName("fightingspirit");
        AURASHORT = new AuraShortage(true, 3694022).setRegistryName("aurashortage");
        event.getRegistry().registerAll(AURA, AURASHORT);
    }

    @SubscribeEvent
    public static void onSoundEventRegistration(final RegistryEvent.Register<SoundEvent> event) {
        aurashatter = new SoundEvent(new ResourceLocation("aurasword", "aurashatter")).setRegistryName("aurashatter");
        event.getRegistry().register(aurashatter);
    }
}