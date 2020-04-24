package baguchan.hunterillager.entity;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.HunterSounds;
import baguchan.hunterillager.entity.ai.GotoBedGoal;
import baguchan.hunterillager.entity.ai.RangedAggroedAttackGoal;
import baguchan.hunterillager.entity.ai.WakeUpGoal;
import baguchan.hunterillager.entity.projectile.BoomerangEntity;
import baguchan.hunterillager.huntertype.HunterType;
import baguchan.hunterillager.huntertype.HunterTypeUtils;
import baguchan.hunterillager.init.HunterItems;
import baguchan.hunterillager.init.HunterTypes;
import baguchan.hunterillager.item.BoomerangItem;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class HunterIllagerEntity extends AbstractIllagerEntity implements IRangedAttackMob {
    private static final Predicate<ItemEntity> field_213665_b = (p_213647_0_) -> {
        return !p_213647_0_.cannotPickup() && p_213647_0_.isAlive() && (ItemStack.areItemStacksEqual(p_213647_0_.getItem(), new ItemStack(HunterItems.BOOMERANG)) || isFoods(p_213647_0_.getItem().getItem()));
    };

    private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION)).setSaved(false);
    private static final DataParameter<Boolean> IS_EATING = EntityDataManager.createKey(HunterIllagerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> TYPE = EntityDataManager.createKey(HunterIllagerEntity.class, DataSerializers.STRING);

    private int foodUseTimer;
    @Nullable
    private BlockPos homePosition;

    private final Inventory inventory = new Inventory(5);

    public static final Predicate<LivingEntity> animalTarget = (p_213440_0_) -> {
        EntityType<?> entitytype = p_213440_0_.getType();
        return entitytype != EntityType.CAT && entitytype != EntityType.FOX && !(p_213440_0_ instanceof TameableEntity) && !(p_213440_0_ instanceof HorseEntity) || entitytype == EntityType.SNOW_GOLEM;
    };

    protected int eattick = 0;
    private int cooldownTicks;

    public HunterIllagerEntity(EntityType<HunterIllagerEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 6;
        ((GroundPathNavigator) this.getNavigator()).setBreakDoors(true);
        this.setDropChance(EquipmentSlotType.OFFHAND, 0.4F);
        this.setCanPickUpLoot(true);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new HunterIllagerEntity.MoveToFoodOrBoomerangGoal<>(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.85F, false) {
            @Override
            public boolean shouldExecute() {
                return !isHolding(Items.BOW) && !isHolding(HunterItems.BOOMERANG) && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(4, new RangedAggroedAttackGoal(this, 0.7D, 40, 16.0F) {
            @Override
            public boolean shouldExecute() {
                return !isHolding(Items.BOW) && isHolding(HunterItems.BOOMERANG) && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(4, new RangedBowAttackGoal(this, 0.7D, 25, 15.0F) {
            @Override
            public boolean shouldExecute() {
                return isHolding(Items.BOW) && !isHolding(HunterItems.BOOMERANG) && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(6, new WakeUpGoal(this));
        this.goalSelector.addGoal(7, new GotoBedGoal(this, 0.7D));
        this.goalSelector.addGoal(8, new MoveToGoal(this, 10.0D, 0.7D));
        this.goalSelector.addGoal(9, new RandomWalkingGoal(this, 0.65D));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(4, (new NearestAttackableTargetGoal(this, AnimalEntity.class, 10, true, false, animalTarget) {
            @Override
            public boolean shouldExecute() {
                return super.shouldExecute() && !isRaidActive() && !isCooldown();
            }
        }).setUnseenMemoryTicks(300));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.35D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(22.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

    public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        this.setEquipmentBasedOnDifficulty(p_213386_2_);
        this.setEnchantmentBasedOnDifficulty(p_213386_2_);
        this.inventory.addItem(new ItemStack(Items.PORKCHOP, 3));

        if (BiomeDictionary.hasType(p_213386_1_.getBiome(new BlockPos(this)), BiomeDictionary.Type.SNOWY)) {
            this.setHunterType(HunterTypes.SNOW);
        }
        return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        if (this.world.rand.nextFloat() < 0.6F) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
        } else {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(HunterItems.BOOMERANG));
            this.inventory.addItem(new ItemStack(HunterItems.BOOMERANG));
        }
    }

    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
        float f = difficulty.getClampedAdditionalDifficulty();
        if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.3F * f) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int) (5.0F + f * (float) this.rand.nextInt(18)), false));
        } else {
            if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.3F) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int) (5.0F + this.rand.nextInt(10)), false));
            }
        }
    }

    protected void registerData() {
        super.registerData();
        this.getDataManager().register(IS_EATING, false);
        this.getDataManager().register(TYPE, HunterTypes.PLAIN.getRegistryName().toString());
    }

    @Override
    public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
        ItemStack itemstack = new ItemStack(Items.BOW);
        Raid raid = this.getRaid();
        int i = 1;
        if (p_213660_1_ > raid.getWaves(Difficulty.NORMAL)) {
            i = 2;
        }

        boolean flag = this.rand.nextFloat() <= raid.getEnchantOdds();
        boolean flag3 = this.rand.nextFloat() <= 0.2;
        if (flag) {
            Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.POWER, i);
            EnchantmentHelper.setEnchantments(map, itemstack);
            if (flag3) {
                this.inventory.addItem(new ItemStack(Items.GOLDEN_APPLE, 1));
            }

        }

        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        if (this.homePosition != null) {
            compound.put("HomeTarget", NBTUtil.writeBlockPos(this.homePosition));
        }

        HunterTypeUtils.setHunterType(compound, this.getHunterType());

        compound.putInt("CooldownTicks", this.cooldownTicks);

        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                listnbt.add(itemstack.write(new CompoundNBT()));
            }
        }

        compound.put("Inventory", listnbt);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        if (compound.contains("HomeTarget")) {
            this.homePosition = NBTUtil.readBlockPos(compound.getCompound("HomeTarget"));
        }

        if (compound.contains("HunterType")) {
            this.setHunterType(HunterTypeUtils.getHunterTypeFromNBT(compound));
        } else {
            this.setHunterType(HunterTypes.PLAIN);
        }

        this.cooldownTicks = compound.getInt("CooldownTicks");

        ListNBT listnbt = compound.getList("Inventory", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.inventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
    }

    public Inventory func_213674_eg() {
        return this.inventory;
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        Item item = itemstack.getItem();
        if (this.isFoods(item)) {
            this.onItemPickup(itemEntity, itemstack.getCount());
            ItemStack itemstack1 = this.inventory.addItem(itemstack);
            if (itemstack1.isEmpty()) {
                itemEntity.remove();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        } else if (item == HunterItems.BOOMERANG) {
            super.updateEquipmentIfNeeded(itemEntity);
        }
    }

    private static boolean isFoods(Item item) {
        return item.isFood() && item != Items.SPIDER_EYE && item != Items.PUFFERFISH;
    }


    public void setMainHome(@Nullable BlockPos p_213726_1_) {
        this.homePosition = p_213726_1_;
    }

    @Nullable
    public BlockPos getMainHome() {
        return this.homePosition;
    }

    public void setHunterType(HunterType hunterType) {
        this.getDataManager().set(TYPE, hunterType.getRegistryName().toString());
    }

    public HunterType getHunterType() {
        return HunterTypeUtils.getHunterFromString(this.getDataManager().get(TYPE));
    }

    public boolean isCooldown() {
        return this.cooldownTicks > 0;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    public void setCooldownTicks(int tick) {
        this.cooldownTicks = tick;
    }

    public void setEatFood(boolean drinkingPotion) {
        this.getDataManager().set(IS_EATING, drinkingPotion);
    }

    public boolean isEatFood() {
        return this.getDataManager().get(IS_EATING);
    }

    public void livingTick() {
        if (!this.world.isRemote && this.isAlive()) {

            if (this.isEatFood()) {
                if (this.foodUseTimer-- <= 0) {
                    this.setEatFood(false);
                    ItemStack itemstack = this.getHeldItemOffhand();
                    if (this.isFoods(itemstack.getItem())) {
                        this.heal((float) itemstack.getItem().getFood().getHealing());
                        ItemStack itemstack1 = itemstack.onItemUseFinish(this.world, this);
                        if (!itemstack1.isEmpty()) {
                            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, itemstack1);
                        }
                    }
                    this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MODIFIER);
                } else if (this.foodUseTimer >= 0 && this.ticksExisted % 4 == 0) {
                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_GENERIC_EAT, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                }
            } else {
                if (this.rand.nextFloat() < 0.005F && this.getHealth() < this.getMaxHealth()) {
                    if (this.getHeldItemOffhand().isEmpty()) {
                        ItemStack food = findFood();
                        this.setItemStackToSlot(EquipmentSlotType.OFFHAND, food);
                    }
                    this.foodUseTimer = this.getHeldItemOffhand().getUseDuration();
                    this.setEatFood(true);

                    IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                    iattributeinstance.removeModifier(MODIFIER);
                    iattributeinstance.applyModifier(MODIFIER);
                }
            }

            if (this.rand.nextFloat() < 7.5E-4F) {
                this.world.setEntityState(this, (byte) 15);
            }

            if (this.ticksExisted % 20 == 0) {
                ItemStack boomerang = findBoomerang();

                if (this.getHeldItem(Hand.MAIN_HAND).isEmpty() && boomerang.getItem() == HunterItems.BOOMERANG) {
                    this.setItemStackToSlot(EquipmentSlotType.MAINHAND, boomerang.copy());
                    boomerang.shrink(1);
                }
            }
        }

        super.livingTick();
    }

    public ItemStack findFood() {

        for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);

            if (!stack.isEmpty() && this.isFoods(stack.getItem())) {
                return stack;
            } else {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    protected ItemStack findBoomerang() {

        for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);

            if (!stack.isEmpty() && stack.getItem() == HunterItems.BOOMERANG) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public SoundEvent getRaidLossSound() {
        return HunterSounds.HUNTER_ILLAGER_LAUGH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VINDICATOR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VINDICATOR_HURT;
    }

    @Override
    public void onKillEntity(LivingEntity entity) {

        super.onKillEntity(entity);
        if (!(entity instanceof AbstractIllagerEntity)) {

            this.playSound(HunterSounds.HUNTER_ILLAGER_LAUGH, this.getSoundVolume() + 0.15F, this.getSoundPitch());

            this.setCooldownTicks(400);
        }
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory != null) {
            for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.inventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack);
                }
            }

        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        if (entityIn instanceof BoomerangEntity && ((BoomerangEntity) entityIn).getShooter() == this) {
            ((BoomerangEntity) entityIn).remove();
            this.setHeldItem(Hand.MAIN_HAND, ((BoomerangEntity) entityIn).getBoomerang());
        }

        super.collideWithEntity(entityIn);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return new ResourceLocation(HunterIllagerCore.MODID, "entity/hunter_illager");
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        net.minecraft.item.ItemStack main = getHeldItemMainhand();
        net.minecraft.item.ItemStack off = getHeldItemOffhand();

        if (isHolding(Items.BOW)) {
            ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
            AbstractArrowEntity abstractarrowentity = ProjectileHelper.fireArrow(this, itemstack, distanceFactor * 1.15F);
            if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
                abstractarrowentity = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
            double d0 = target.getPosX() - this.getPosX();
            double d1 = target.getBoundingBox().minY + (double) (target.getHeight() / 3.0F) - abstractarrowentity.getPosY();
            double d2 = target.getPosZ() - this.getPosZ();
            double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
            abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (10 - this.world.getDifficulty().getId() * 4));
            this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            world.addEntity(abstractarrowentity);
        } else {
            //world.playSound(null, entity.posX, entity.posY, entity.posZ, HunterSounds.ITEM_BOOMERANG_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);

            this.playSound(SoundEvents.ENTITY_EGG_THROW, 1.0f, 1.0f);

            if (main.getItem() instanceof BoomerangItem) {
                BoomerangEntity projectile = new BoomerangEntity(world, this, main.copy());
                projectile.shoot(this, this.rotationPitch, this.rotationYaw, 0.0F, 1.0F, 1.0F);
                world.addEntity(projectile);
                main.shrink(1);
            } else if (off.getItem() instanceof BoomerangItem) {
                BoomerangEntity projectile = new BoomerangEntity(world, this, off.copy());
                projectile.shoot(this, this.rotationPitch, this.rotationYaw, 0.0F, 1.0F, 1.0F);
                world.addEntity(projectile);
                off.shrink(1);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public AbstractIllagerEntity.ArmPose getArmPose() {
        if (this.isHolding(Items.BOW) || this.isHolding(HunterItems.BOOMERANG)) {
            return this.isAggressive() ? AbstractIllagerEntity.ArmPose.BOW_AND_ARROW : AbstractIllagerEntity.ArmPose.CROSSED;
        } else {
            return this.isAggressive() ? ArmPose.ATTACKING : AbstractIllagerEntity.ArmPose.CROSSED;
        }

    }

    class MoveToGoal extends Goal {
        final HunterIllagerEntity hunterIllager;
        final double field_220848_b;
        final double speed;

        MoveToGoal(HunterIllagerEntity hunterillager, double distance, double speed) {
            this.hunterIllager = hunterillager;
            this.field_220848_b = distance;
            this.speed = speed;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            HunterIllagerEntity.this.navigator.clearPath();
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            BlockPos blockpos = this.hunterIllager.getMainHome();
            return blockpos != null && this.func_220846_a(blockpos, this.hunterIllager.world.isDaytime() ? this.field_220848_b : this.field_220848_b * 0.8F) && !isRaidActive();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            BlockPos blockpos = this.hunterIllager.getMainHome();
            if (blockpos != null && HunterIllagerEntity.this.navigator.noPath()) {
                if (this.func_220846_a(blockpos, 10.0D)) {
                    Vec3d vec3d = (new Vec3d((double) blockpos.getX() - this.hunterIllager.getPosX(), (double) blockpos.getY() - this.hunterIllager.getPosY(), (double) blockpos.getZ() - this.hunterIllager.getPosZ())).normalize();
                    Vec3d vec3d1 = vec3d.scale(10.0D).add(this.hunterIllager.getPosX(), this.hunterIllager.getPosY(), this.hunterIllager.getPosZ());
                    HunterIllagerEntity.this.navigator.tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
                } else {
                    HunterIllagerEntity.this.navigator.tryMoveToXYZ((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), this.speed);
                }
            }

        }

        private boolean func_220846_a(BlockPos p_220846_1_, double p_220846_2_) {
            return !p_220846_1_.withinDistance(this.hunterIllager.getPositionVec(), p_220846_2_);
        }
    }

    public class MoveToFoodOrBoomerangGoal<T extends HunterIllagerEntity> extends Goal {
        private final T illager;

        public MoveToFoodOrBoomerangGoal(T p_i50572_2_) {
            this.illager = p_i50572_2_;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if ((this.illager.getHeldItem(Hand.MAIN_HAND).isEmpty() || this.illager.getHeldItem(Hand.OFF_HAND).isEmpty())) {
                List<ItemEntity> list = this.illager.world.getEntitiesWithinAABB(ItemEntity.class, this.illager.getBoundingBox().grow(16.0D, 8.0D, 16.0D), HunterIllagerEntity.field_213665_b);
                if (!list.isEmpty()) {
                    return this.illager.getNavigator().tryMoveToEntityLiving(list.get(0), 0.85D);
                }
            }

            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (this.illager.getNavigator().getTargetPos().withinDistance(this.illager.getPositionVec(), 1.414D)) {
                List<ItemEntity> list = this.illager.world.getEntitiesWithinAABB(ItemEntity.class, this.illager.getBoundingBox().grow(4.0D, 4.0D, 4.0D), HunterIllagerEntity.field_213665_b);
                if (!list.isEmpty()) {
                    this.illager.updateEquipmentIfNeeded(list.get(0));
                }
            }

        }
    }
}
