package AuraSword;

import AuraSword.items.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

//    @GameRegistry.ObjectHolder("AuraSword:AuraSword")
//    public static AuraSword auraSword;
    @GameRegistry.ObjectHolder("AuraSword:Sheath")
    public static Sheath sheath;
//    @GameRegistry.ObjectHolder("AuraSword:AuraSwordSheathed")
//    public static AuraSwordSheathed auraSwordSheathed;
    @GameRegistry.ObjectHolder("AuraSword:AuraSwordDefault")
    public static AuraSwordDefault auraSwordDefault;
    @GameRegistry.ObjectHolder("AuraSword:AuraSwordActive")
    public static AuraSwordActive auraSwordActive;
    @GameRegistry.ObjectHolder("AuraSword:Rootswt")
    public static Roots roots;
    @SideOnly(Side.CLIENT)
    public static void initModels() {
//        auraSword.initModel();
        sheath.initModel();
//        auraSwordSheathed.initModel();
        auraSwordDefault.initModel();
        auraSwordActive.initModel();
        roots.initModel();
    }

}
