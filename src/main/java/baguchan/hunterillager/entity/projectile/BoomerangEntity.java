package baguchan.hunterillager.entity.projectile;

import baguchan.hunterillager.event.BoomerangEventFactory;
import baguchan.hunterillager.init.HunterEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class BoomerangEntity extends ThrowableEntity {
    private static final DataParameter<Byte> LOYALTY_LEVEL = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> PIERCING_LEVEL = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> RETURNING = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<ItemStack> BOOMERANG = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.ITEMSTACK);


    private int entityHits;
    private int totalHits;
    private int flyTick;

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, World world) {
        super(type, world);
    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, World world, @Nullable LivingEntity shootingEntity, ItemStack boomerang) {
        super(type, shootingEntity, world);
        this.setShooter(shootingEntity);
        this.setBoomerang(boomerang);
        this.dataManager.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyaltyModifier(boomerang));
        this.dataManager.set(PIERCING_LEVEL, (byte) EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, boomerang));
        this.totalHits = 0;
    }

    public BoomerangEntity(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(HunterEntityRegistry.BOOMERANG, world);
    }

    public BoomerangEntity(World world, LivingEntity entity, ItemStack boomerang) {
        this(HunterEntityRegistry.BOOMERANG, world, entity, boomerang);
    }

    private void onHitFluid(BlockRayTraceResult result) {
        double velocity = this.getVelocity();

        double horizontal = this.getMotion().getY() * this.getMotion().getY();
        if (!this.world.isRemote) {
            if (result.getType() == RayTraceResult.Type.BLOCK && result.isInside()) {
                if (velocity >= 0.65F && horizontal < 0.175F) {

                    if (!this.world.getFluidState(result.getPos()).isEmpty() && this.world.getFluidState(result.getPos()).isTagged(FluidTags.WATER)) {

                        this.setMotion(this.getMotion().getX(), MathHelper.clamp(this.getMotion().getY() + 0.1F, -0.1F, 0.3F), this.getMotion().getZ());

                        this.isAirBorne = true;
                    }
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);

        boolean returnToOwner = false;

        int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);
        int piercingLevel = this.dataManager.get(PIERCING_LEVEL);

        Entity entity = this.func_234616_v_();

        if (result.getEntity() != this.func_234616_v_()) {

            if (!(this.isReturning() && loyaltyLevel > 0)) {
                Entity shooter = this.func_234616_v_();
                int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, this.getBoomerang());
                ((EntityRayTraceResult) result).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, shooter), (float) (3 * Math.sqrt(this.getMotion().getX() * this.getMotion().getX() + (this.getMotion().getY() * this.getMotion().getY()) * 0.5F + this.getMotion().getZ() * this.getMotion().getZ()) + Math.min(1, sharpness) + Math.max(0, sharpness - 1) * 0.5) + 0.5F * piercingLevel);


                if (shooter instanceof LivingEntity) {
                    this.getBoomerang().damageItem(1, (LivingEntity) shooter, (p_222182_1_) -> {
                    });
                }

                double speed = this.getSpeed();

                //this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), HunterSounds.ITEM_BOOMERANG_HIT, SoundCategory.BLOCKS, 0.5F, 1.0F);
                if (piercingLevel < 1 || entityHits >= piercingLevel || speed < 0.4F) {
                    returnToOwner = true;
                    this.totalHits += 1;
                }

                this.entityHits += 1;
            }
        }

        if (returnToOwner && !isReturning()) {
            if (this.func_234616_v_() != null && this.shouldReturnToThrower() && EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, this.getBoomerang()) > 0) {
                Entity shooter = this.func_234616_v_();
                this.world.playSound(null, shooter.getPosition(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);
                Vector3d motion = this.getMotion();

                double motionX = motion.getX();
                double motionY = motion.getY();
                double motionZ = motion.getZ();

                motionX = -motionX;
                motionZ = -motionZ;

                this.setMotion(motionX, motionY, motionZ);

                this.markVelocityChanged();

                if (loyaltyLevel > 0 && !this.isReturning()) {
                    if (entity != null) {
                        this.world.playSound(null, entity.getPosition(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);
                        this.setReturning(true);
                    }
                }
            } else {
                Vector3d motion = this.getMotion();

                double motionX = motion.getX();
                double motionY = motion.getY();
                double motionZ = motion.getZ();

                motionX = -motionX;
                motionZ = -motionZ;

                this.setMotion(motionX, motionY, motionZ);

                this.markVelocityChanged();

                if (loyaltyLevel > 0 && !this.isReturning()) {
                    if (entity != null) {
                        this.world.playSound(null, entity.getPosition(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);
                        this.setReturning(true);
                    }
                }
            }
        }
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult result) {
        super.func_230299_a_(result);

        BlockPos pos = result.getPos();
        BlockState state = this.world.getBlockState(pos);
        SoundType soundType = state.getSoundType(this.world, pos, this);
        if (this.isReturning()) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), soundType.getHitSound(), SoundCategory.BLOCKS, soundType.getVolume() * 0.26f, soundType.getPitch());
        }
        //this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), HunterSounds.ITEM_BOOMERANG_HIT, SoundCategory.BLOCKS, 0.5F, 1.0F);
        this.totalHits++;

        BlockState blockstate = this.world.getBlockState(result.getPos());

        int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);
        int piercingLevel = this.dataManager.get(PIERCING_LEVEL);
        Entity entity = this.func_234616_v_();


        if (!isReturning() && !blockstate.getCollisionShape(this.world, result.getPos()).isEmpty()) {
            Direction face = result.getFace();

            Vector3d motion = this.getMotion();

            double motionX = motion.getX();
            double motionY = motion.getY();
            double motionZ = motion.getZ();

            if (face == Direction.EAST)
                motionX = -motionX;
            else if (face == Direction.SOUTH)
                motionZ = -motionZ;
            else if (face == Direction.WEST)
                motionX = -motionX;
            else if (face == Direction.NORTH)
                motionZ = -motionZ;
            else if (face == Direction.UP)
                motionY = -motionY;
            else if (face == Direction.DOWN)
                motionY = -motionY;

            this.setMotion(motionX, motionY, motionZ);

            if (loyaltyLevel > 0 && !this.isReturning() && this.totalHits >= this.getBounceLevel()) {
                if (entity != null) {
                    this.world.playSound(null, entity.getPosition(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);
                    this.setReturning(true);
                }
            }
        }

        this.doBlockCollisions();
    }

    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);

        int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);

        //If loyalty is 0, it will bounce several times and then drop.
        if (loyaltyLevel < 1 && this.totalHits >= this.getBounceLevel()) {
            if (!this.world.isRemote()) {
                this.drop(this.getPosX(), this.getPosY(), this.getPosZ());
            }
        }
    }

    private boolean shouldReturnToThrower() {
        Entity entity = this.func_234616_v_();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        super.onCollideWithPlayer(entityIn);
        if (!this.world.isRemote && this.flyTick >= 4 && entityIn == this.func_234616_v_()) {
            this.drop(this.func_234616_v_().getPosX(), this.func_234616_v_().getPosY(), this.func_234616_v_().getPosZ());
        }
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        super.applyEntityCollision(entityIn);
        if (!this.world.isRemote && this.flyTick >= 4 && entityIn == this.func_234616_v_()) {
            this.drop(this.func_234616_v_().getPosX(), this.func_234616_v_().getPosY(), this.func_234616_v_().getPosZ());
        }
    }

    public void drop(double x, double y, double z) {

        if (!(this.func_234616_v_() instanceof PlayerEntity) || (this.func_234616_v_() instanceof PlayerEntity && !((PlayerEntity) this.func_234616_v_()).isCreative())) {
            this.world.addEntity(new ItemEntity(this.world, x, y, z, this.getBoomerang().copy()));
        }

        this.remove();
    }

    @Override
    public void tick() {
        super.tick();

        this.flyTick++;
        Vector3d vec3d = this.getMotion();

        Vector3d vec3d1 = this.getPositionVec();

        Vector3d vec3d2 = new Vector3d(this.getPosX() + this.getMotion().getX(), this.getPosY() + this.getMotion().getY(), this.getPosZ() + this.getMotion().getZ());

        BlockRayTraceResult fluidRaytraceResult = this.world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, this));

        this.onHitFluid(fluidRaytraceResult);
        BoomerangEventFactory.boomerangSpeedTick(this, vec3d);

        float f = MathHelper.sqrt(this.getMotion().getX() * this.getMotion().getX() + this.getMotion().getZ() * this.getMotion().getZ());
        this.rotationYaw = (float) (MathHelper.atan2(this.getMotion().getX(), this.getMotion().getZ()) * (double) (180F / (float) Math.PI));

        for (this.rotationPitch = (float) (MathHelper.atan2(this.getMotion().getY(), (double) f) * (double) (180F / (float) Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);

        Entity entity = this.func_234616_v_();

        if (loyaltyLevel > 0 && !this.isReturning() && this.flyTick >= 100) {
            if (entity != null) {
                this.world.playSound(null, entity.getPosition(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);
                this.setReturning(true);
            }
        }

        if (loyaltyLevel > 0 && entity != null && !this.shouldReturnToThrower() && this.isReturning()) {
            if (!this.world.isRemote) {
                this.drop(this.getPosX(), this.getPosY(), this.getPosZ());
            }

            this.remove();
        } else if (loyaltyLevel > 0 && entity != null && this.isReturning()) {
            this.noClip = true;
            Vector3d vec3d3 = new Vector3d(entity.getPosX() - this.getPosX(), entity.getPosY() + (double) entity.getEyeHeight() - this.getPosY(), entity.getPosZ() - this.getPosZ());
            if (this.world.isRemote) {
                this.lastTickPosY = this.getPosY();
            }

            double d0 = 0.05D * (double) loyaltyLevel;
            this.setMotion(this.getMotion().scale(0.95D).add(vec3d3.normalize().scale(d0)));
        }
        this.collideWithNearbyEntities();
    }


    /*
     * This method is that boomerang uses to determine if it affects the entity
     * If set to true, will allow damage
     */
    protected boolean func_230298_a_(Entity p_230298_1_) {
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.canBeCollidedWith()) {
            Entity entity = this.func_234616_v_();
            return entity == null || !entity.isRidingSameEntity(p_230298_1_);
        } else {
            return false;
        }
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
    }

    @Override
    protected void registerData() {
        this.dataManager.register(LOYALTY_LEVEL, (byte) 0);
        this.dataManager.register(PIERCING_LEVEL, (byte) 0);
        this.dataManager.register(RETURNING, false);
        this.dataManager.register(BOOMERANG, ItemStack.EMPTY);
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);

        nbt.put("boomerang", this.getBoomerang().write(new CompoundNBT()));
        nbt.putByte("entityHits", (byte) this.entityHits);
        nbt.putByte("totalHits", (byte) this.totalHits);
        nbt.putBoolean("returning", this.isReturning());
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);

        this.setBoomerang(ItemStack.read(nbt.getCompound("boomerang")));
        this.entityHits = nbt.getByte("entityHits");
        this.totalHits = nbt.getByte("totalHits");
        this.setReturning(nbt.getBoolean("returning"));
        this.dataManager.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyaltyModifier(this.getBoomerang()));
        this.dataManager.set(PIERCING_LEVEL, (byte) EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, this.getBoomerang()));
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    /*
     * When HunterIllager Shooter or NonPlayer Shooter collide Boomerang
     * drop boomerang item
     */
    protected void collideWithNearbyEntities() {
        if (isReturning()) {
            List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), EntityPredicates.pushableBy(this));
            if (!list.isEmpty()) {
                for (int l = 0; l < list.size(); ++l) {
                    Entity entity = list.get(l);

                    if (entity == this.func_234616_v_()) {
                        this.drop(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                    }
                }
            }
        }
    }

    private int getBounceLevel() {
        int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);

        if (loyaltyLevel > 0) {
            return 0;
        } else {
            return 8;
        }
    }

    public boolean isReturning() {
        return this.dataManager.get(RETURNING);
    }

    public ItemStack getBoomerang() {
        return this.dataManager.get(BOOMERANG);
    }

    public double getSpeed() {
        return Math.sqrt(this.getMotion().getX() * this.getMotion().getX() + this.getMotion().getY() * this.getMotion().getY() + this.getMotion().getZ() * this.getMotion().getZ());
    }

    public double getVelocity() {
        return Math.sqrt(this.getMotion().getX() * this.getMotion().getX() + this.getMotion().getZ() * this.getMotion().getZ());
    }

    public int getPiercingLevel() {
        return this.dataManager.get(PIERCING_LEVEL);
    }

    public void setReturning(boolean returning) {
        this.dataManager.set(RETURNING, returning);
    }

    public void setBoomerang(ItemStack stack) {
        this.dataManager.set(BOOMERANG, stack);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}