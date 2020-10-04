package baguchan.hunterillager.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;

public class QuiverItem extends ArrowItem {
    public QuiverItem(Properties group) {
        super(group);
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, PlayerEntity player) {
        return true;
    }
}
