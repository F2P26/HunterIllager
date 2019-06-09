package baguchan.hunterillager.entity;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.HunterSounds;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityHunterIllager extends AbstractIllagerEntity implements IRangedAttackMob {
    private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION)).setSaved(false);
    private static final DataParameter<Boolean> IS_DRINKING = EntityDataManager.createKey(WitchEntity.class, DataSerializers.field_187198_h);
    private int potionUseTimer;
    private BlockPos homePosition = BlockPos.ZERO;
    /** If -1 there is no maximum distance */
    private float maximumHomeDistance = -1.0F;

    public static final Predicate<LivingEntity> animalTarget = (p_213440_0_) -> {
        EntityType<?> entitytype = p_213440_0_.getType();
        return entitytype == EntityType.CHICKEN ||entitytype == EntityType.COW || entitytype == EntityType.PIG ||entitytype == EntityType.RABBIT || entitytype == EntityType.SNOW_GOLEM;
    };

    protected int eattick = 0;
    private int cooldownTicks;

    public EntityHunterIllager(EntityType<EntityHunterIllager> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 4;
        ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
        this.setDropChance(EquipmentSlotType.OFFHAND, 0.4F);
    }



    @Override
    protected void initEntityAI()
    {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimGoal(this));
        this.field_70714_bg.addTask(2, new OpenDoorGoal(this,true));
        this.field_70714_bg.addTask(4, new RangedBowAttackGoal<>(this, 0.65D, 20, 15.0F));
        this.field_70714_bg.addTask(8, new RandomWalkingGoal(this, 0.75D));
        this.field_70714_bg.addTask(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.field_70714_bg.addTask(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.field_70715_bh.addTask(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).func_220794_a());
        this.field_70715_bh.addTask(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
        this.field_70715_bh.addTask(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
        this.field_70715_bh.addTask(3, (new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
        this.field_70715_bh.addTask(4, (new NearestAttackableTargetGoal(this, AnimalEntity.class, 10, true,false,animalTarget){
            @Override
            public boolean shouldExecute() {
                return super.shouldExecute() && !isCooldown();
            }
        }).setUnseenMemoryTicks(300));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(22.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

    public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
        return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    protected void registerData() {
        super.registerData();
        this.getDataManager().register(IS_DRINKING, false);
    }

    @Override
    public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
        ItemStack itemstack = new ItemStack(Items.BOW);
        Raid raid = this.func_213663_ek();
        int i = 1;
        if (p_213660_1_ > raid.func_221306_a(Difficulty.NORMAL)) {
            i = 2;
        }

        boolean flag = this.rand.nextFloat() <= raid.func_221308_w();
        boolean flag2 = this.rand.nextFloat() <= 0.25;
        if (flag) {
            Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.POWER, i);
            EnchantmentHelper.setEnchantments(map, itemstack);
            if(flag2){
                Map<Enchantment, Integer> map2 = Maps.newHashMap();
                map2.put(Enchantments.FLAME, 1);
                EnchantmentHelper.setEnchantments(map2, itemstack);
            }
        }

        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        compound.putInt("CooldownTicks", this.cooldownTicks);

        compound.putInt("HomePosX", homePosition.getX());
        compound.putInt("HomePosY", homePosition.getY());
        compound.putInt("HomePosZ", homePosition.getZ());

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        this.cooldownTicks = compound.getInt("CooldownTicks");

        homePosition = new BlockPos(compound.getInt("HomePosX"), compound.getInt("HomePosY"), compound.getInt("HomePosZ"));
    }


    /**
     * Sets home position and max distance for it
     */
    public void setHomePosAndDistance(BlockPos pos, int distance)
    {
        this.homePosition = pos;
        this.maximumHomeDistance = (float)distance;
    }


    public BlockPos getHomePosition()
    {
        return this.homePosition;
    }


    public boolean isCooldown()
    {
        return this.cooldownTicks > 0;
    }

    public int getCooldownTicks()
    {
        return this.cooldownTicks;
    }

    public void setCooldownTicks(int tick)
    {
        this.cooldownTicks = tick;
    }

    public void setDrinkingPotion(boolean drinkingPotion) {
        this.getDataManager().set(IS_DRINKING, drinkingPotion);
    }

    public boolean isDrinkingPotion() {
        return this.getDataManager().get(IS_DRINKING);
    }

    public void livingTick() {
        if (!this.world.isRemote && this.isAlive()) {

            if (this.isDrinkingPotion()) {
                if (this.potionUseTimer-- <= 0) {
                    this.setDrinkingPotion(false);
                    ItemStack itemstack = this.getHeldItemOffhand();
                    this.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                    if (itemstack.getItem() == Items.POTION) {
                        List<EffectInstance> list = PotionUtils.getEffectsFromStack(itemstack);
                        if (list != null) {
                            for(EffectInstance effectinstance : list) {
                                this.addPotionEffect(new EffectInstance(effectinstance));
                            }
                        }
                    }
                }
            } else {
                Potion potion = null;
                if (this.rand.nextFloat() < 0.0046F && this.getHealth() < this.getMaxHealth()) {
                    potion = Potions.field_185250_v;
                }

                if (potion != null) {
                    this.setItemStackToSlot(EquipmentSlotType.OFFHAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion));
                    this.potionUseTimer = this.getHeldItemOffhand().getUseDuration();
                    this.setDrinkingPotion(true);
                    this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                    IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                    iattributeinstance.removeModifier(MODIFIER);
                    iattributeinstance.applyModifier(MODIFIER);
                }
            }

            if (this.rand.nextFloat() < 7.5E-4F) {
                this.world.setEntityState(this, (byte)15);
            }
        }

        super.livingTick();
    }

    public SoundEvent func_213654_dW() {
        return HunterSounds.HUNTER_ILLAGER_LAUGH;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VINDICATOR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VINDICATOR_HURT;
    }

    @Override
    public void onKillEntity(LivingEntity entity) {

        super.onKillEntity(entity);
        if(!(entity instanceof AbstractIllagerEntity)) {

            this.playSound(HunterSounds.HUNTER_ILLAGER_LAUGH, this.getSoundVolume() + 0.15F, this.getSoundPitch());

            this.setCooldownTicks(400);
        }
    }

    @Override
    protected ResourceLocation getLootTable()
    {
        return new ResourceLocation(HunterIllagerCore.MODID,"entity/hunter_illager");
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.func_213356_f(this.getHeldItem(ProjectileHelper.func_221274_a(this, Items.BOW)));
        AbstractArrowEntity abstractarrowentity = ProjectileHelper.func_221272_a(this, itemstack, distanceFactor);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
            abstractarrowentity = ((net.minecraft.item.BowItem)this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
        double d0 = target.posX - this.posX;
        double d1 = target.getBoundingBox().minY + (double)(target.getHeight() / 3.0F) - abstractarrowentity.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
        abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.func_217376_c(abstractarrowentity);
    }

    @OnlyIn(Dist.CLIENT)
    public AbstractIllagerEntity.ArmPose getArmPose() {
        return this.func_213398_dR() ? AbstractIllagerEntity.ArmPose.BOW_AND_ARROW : AbstractIllagerEntity.ArmPose.CROSSED;

    }
}
