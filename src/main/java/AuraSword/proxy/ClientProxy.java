package AuraSword.proxy;

import AuraSword.ModItems;
import AuraSword.client.LayerSheat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

import static AuraSword.AuraSwordMod.MODID;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends AuraSword.proxy.CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        addRenderLayers();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModItems.initModels();
    }

    @SubscribeEvent
    public void registerRenderers() {
    }


    @SubscribeEvent
    public static void registerTextures(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation("aurasword:particle/roots"));
        event.getMap().registerSprite(new ResourceLocation("aurasword:particle/beam"));
        event.getMap().registerSprite(new ResourceLocation("aurasword:particle/beam2"));
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String name) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(MODID + ":" + name, "inventory"));
    }

    private static void addRenderLayers() {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();

        addLayersToSkin(skinMap.get("default"), false);
    }

    private static void addLayersToSkin(RenderPlayer renderPlayer, boolean slim) {
        renderPlayer.addLayer(new LayerSheat(renderPlayer));

    }
}
