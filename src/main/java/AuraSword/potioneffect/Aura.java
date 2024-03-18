package AuraSword.potioneffect;

import net.minecraft.potion.Potion;

public class Aura extends Potion {
    public Aura(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setPotionName("\u00A7cFighting Spirit");
    }
}
