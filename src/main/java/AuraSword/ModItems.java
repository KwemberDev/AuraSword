package AuraSword;

import AuraSword.items.AuraSword;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    @GameRegistry.ObjectHolder("AuraSword:AuraSword")
    public static AuraSword auraSword;
    @SideOnly(Side.CLIENT)
    public static void initModels() {
        auraSword.initModel();
    }

}
