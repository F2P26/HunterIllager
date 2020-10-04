package baguchan.hunterillager.item;

import baguchan.hunterillager.init.HunterItems;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.List;

public class QuiverItem extends ArrowItem {
    public QuiverItem(Properties group) {
        super(group);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        ItemStack itemstack2 = playerIn.getHeldItem(Hand.OFF_HAND);

        if (itemstack2.getItem() instanceof ArrowItem && itemstack2.getItem() != HunterItems.QUIVER && getProjectiles(itemstack).size() < 6) {
            addArrow(playerIn, itemstack, itemstack2, playerIn.isCreative());
            playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
            return ActionResult.resultConsume(itemstack);
        }

        return ActionResult.resultFail(itemstack);
    }

    private static boolean addArrow(LivingEntity livingEntity, ItemStack stack, ItemStack arrow, boolean isCreative) {
        if (arrow.isEmpty()) {
            return false;
        } else {
            boolean flag = isCreative && arrow.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !isCreative) {
                itemstack = arrow.split(arrow.getCount());
                if (arrow.isEmpty() && livingEntity instanceof PlayerEntity) {
                    ((PlayerEntity) livingEntity).inventory.deleteStack(arrow);
                }
            } else {
                itemstack = arrow.copy();
            }

            addProjectile(stack, itemstack);
            return true;
        }
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, PlayerEntity player) {
        return true;
    }

    public boolean hasArrow(ItemStack quiver, PlayerEntity player) {
        return getProjectiles(quiver).stream().anyMatch((p_220010_1_) -> {
            return p_220010_1_.getItem() instanceof ArrowItem;
        });
    }

    public static void addProjectile(ItemStack quiver, ItemStack projectile) {
        CompoundNBT compoundnbt = quiver.getOrCreateTag();
        ListNBT listnbt;
        if (compoundnbt.contains("Projectiles", 9)) {
            listnbt = compoundnbt.getList("Projectiles", 10);
        } else {
            listnbt = new ListNBT();
        }

        CompoundNBT compoundnbt1 = new CompoundNBT();
        projectile.write(compoundnbt1);
        listnbt.add(compoundnbt1);
        compoundnbt.put("Projectiles", listnbt);
    }

    public static List<ItemStack> getProjectiles(ItemStack stack) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundNBT compoundnbt = stack.getTag();
        if (compoundnbt != null && compoundnbt.contains("Projectiles", 9)) {
            ListNBT listnbt = compoundnbt.getList("Projectiles", 10);
            if (listnbt != null) {
                for (int i = 0; i < listnbt.size(); ++i) {
                    CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                    list.add(ItemStack.read(compoundnbt1));
                }
            }
        }

        return list;
    }

    public static ItemStack getProjectileFromQuivers(ItemStack quiver) {
        if (quiver.hasTag() && quiver.getTag().contains("Projectiles", 9)) {
            List<ItemStack> list = getProjectiles(quiver);
            for (int i = 0; i < list.size(); ++i) {
                ItemStack stack = list.get(i);

                if (!stack.isEmpty()) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private static void clearProjectiles(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTag();
        if (compoundnbt != null) {
            ListNBT listnbt = compoundnbt.getList("Projectiles", 9);
            listnbt.clear();
            compoundnbt.put("Projectiles", listnbt);
        }

    }

    public static void consumeProjectiles(ItemStack stack) {
        List<ItemStack> list = getProjectiles(stack);
        List<ItemStack> list2 = Lists.newArrayList();

        boolean hasArrow = false;

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);

            if (!hasArrow && !itemstack.isEmpty()) {
                itemstack.shrink(1);

                hasArrow = true;
            }

            if (!itemstack.isEmpty()) {
                list2.add(itemstack);
            }
        }

        clearProjectiles(stack);

        for (int i = 0; i < list2.size(); ++i) {
            ItemStack itemstack = list2.get(i);

            addProjectile(stack, itemstack);
        }
    }

}
