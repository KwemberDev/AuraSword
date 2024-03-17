package AuraSword.client;

import AuraSword.items.AuraSwordActive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static AuraSword.config.Config.displaySheath;
import static AuraSword.items.AuraSwordActive.TEXTURE_KEY;

public class LayerSheat extends LayerRender {

    private static final ModelBase sheath = new ModelSheath();
    private static final ResourceLocation SHEATH_TEXTURE = new ResourceLocation("aurasword", "textures/items/sheathmodeltexture.png");

    public LayerSheat(RenderPlayer renderPlayer) {
        super(renderPlayer);
    }

    @Override
    protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack mainHandItem = player.getHeldItemMainhand();
        ItemStack offHandItem = player.getHeldItemOffhand();
        // if the player is holding auraswordactive in main of offhand in aura form, render sheath
        if (((mainHandItem.getItem() instanceof AuraSwordActive && mainHandItem.hasTagCompound() && mainHandItem.getTagCompound().getBoolean(TEXTURE_KEY)) || (offHandItem.getItem() instanceof AuraSwordActive && offHandItem.hasTagCompound() && offHandItem.getTagCompound().getBoolean(TEXTURE_KEY))) && displaySheath) {


            Minecraft.getMinecraft().getTextureManager().bindTexture(SHEATH_TEXTURE);

            // Position the sheath on the player's back
            GlStateManager.translate(0, 0, -0.1F);

            // Rotate the sheath to match the player's rotation
            GlStateManager.rotate(0, 0, 1, 0);
            GlStateManager.rotate(0, 1, 0, 0);

            sheath.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
}
