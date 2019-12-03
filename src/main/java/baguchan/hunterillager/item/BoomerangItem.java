package baguchan.hunterillager.item;

import baguchan.hunterillager.entity.projectile.BoomerangEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class BoomerangItem extends Item {

    public BoomerangItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.SHARPNESS || enchantment == Enchantments.LOYALTY || enchantment == Enchantments.PIERCING;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        if (!world.isRemote) {
            int heldTime = this.getUseDuration(stack) - timeLeft;
            float velocity = 1.5F * BowItem.getArrowVelocity(heldTime);

            BoomerangEntity projectile = new BoomerangEntity(world, entity, stack.copy());
            projectile.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, velocity, 1.0F);

            //projectile.setCandrop(true);
            world.addEntity(projectile);
            //world.playSound(null, entity.posX, entity.posY, entity.posZ, HunterSounds.ITEM_BOOMERANG_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);

            world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);

            if (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).isCreative()) {
                stack.shrink(1);
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 1;
    }
}