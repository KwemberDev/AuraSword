package AuraSword.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;

import static AuraSword.config.Config.displaySheath;
import static AuraSword.proxy.CommonProxy.config;

public class ModelSheath extends ModelBase {

    public ModelRenderer sheath;

    public ModelSheath() {

        // size of the sheath texture
        this.textureWidth = 384;
        this.textureHeight = 384;

        // make add boxes to the renderer, texture is placed on said boxes
        sheath = new ModelRenderer(this, 0, 0);
        for (float i = 1; i <= 10; i += 0.02F) {
            sheath.addBox(0, 0, i, 192, 192, 0);
        }


        // ps: dont question my method of realizing 3d from a texture, its funky i know.
    }


    @Override
    public void render(Entity entity, float partialticks, float f1, float f2, float f3, float f4, float scale) {
        // downscale the texture, place it in the right position on the players back, and rotate it. then render it
        boolean hasChestplate = !((EntityPlayer) entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
        if (hasChestplate) {
            GlStateManager.translate(0, 0, 0.06);
        }
        if (entity.isSneaking()) {
            GlStateManager.translate(0,0.2,-0.0);
            GlStateManager.rotate(-27F,-10,0, 0);
        }
        GlStateManager.scale(1/10F, 1/10F, 1/10F);
        GlStateManager.translate(-6D, -2, 2.4);
        GlStateManager.rotate(0, 0, 0, 0);
        sheath.render(scale);
    }
}