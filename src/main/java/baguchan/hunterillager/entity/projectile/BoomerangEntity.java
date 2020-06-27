package baguchan.hunterillager.entity.projectile;

import baguchan.hunterillager.event.BoomerangEventFactory;
import baguchan.hunterillager.init.HunterEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BoomerangEntity extends Entity {
    private static final DataParameter<Byte> LOYALTY_LEVEL = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> PIERCING_LEVEL = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> RETURNING = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<ItemStack> BOOMERANG = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.ITEMSTACK);

    @Nullable
    private UUID shootingEntity;
    private Vector3d throwPos;
    private int entityHits;
    private int totalHits;
    private int flyTick;

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, World world) {
        super(type, world);
    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, World world, @Nullable LivingEntity shootingEntity, ItemStack boomerang) {
        super(type, world);
        this.setShooter(shootingEntity);
        this.setBoomerang(boomerang);
        this.dataManager.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyaltyModifier(boomerang));
        this.dataManager.set(PIERCING_LEVEL, (byte) EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, boomerang));
        this.throwPos = new Vector3d(shootingEntity == null ? 0 : shootingEntity.getPosX(), shootingEntity == null ? 0 : shootingEntity.getPosY() + shootingEntity.getEyeHeight() - 0.1, shootingEntity == null ? 0 : shootingEntity.getPosZ());
        this.setPosition(this.throwPos.x, this.throwPos.y, this.throwPos.z);
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

    private void onHit(RayTraceResult result) {
        if (!this.world.isRemote) {
            boolean returnToOwner = result.getType() == RayTraceResult.Type.BLOCK;

            if (result.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos = ((BlockRayTraceResult) result).getPos();
                BlockState state = this.world.getBlockState(pos);
                SoundType soundType = state.getSoundType(this.world, pos, this);
                this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), soundType.getHitSound(), SoundCategory.BLOCKS, soundType.getVolume() * 0.26f, soundType.getPitch());
                //this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), HunterSounds.ITEM_BOOMERANG_HIT, SoundCategory.BLOCKS, 0.5F, 1.0F);
                this.totalHits++;

                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) result;
                BlockState blockstate = this.world.getBlockState(blockraytraceresult.getPos());

                //blockstate.onProjectileCollision(this.world, state, blockraytraceresult, this);
                this.doBlockCollisions();
            }
            int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);
            int piercingLevel = this.dataManager.get(PIERCING_LEVEL);

            if (result.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) result).getEntity() != this.getShooter()) {

                if (!(this.isReturning() && loyaltyLevel > 0)) {
                    Entity shooter = this.getShooter();
                    int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, this.getBoomerang());
                    ((EntityRayTraceResult) result).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, shooter), (float) (3 * Math.sqrt(this.getMotion().getX() * this.getMotion().getX() + (this.getMotion().getY() * this.getMotion().getY()) * 0.5F + this.getMotion().getZ() * this.getMotion().getZ()) + Math.min(1, sharpness) + Math.max(0, sharpness - 1) * 0.5) + 0.5F * piercingLevel);


                    if (shooter instanceof LivingEntity) {
                        this.getBoomerang().damageItem(1, (LivingEntity) shooter, (p_222182_1_) -> {
                        });
                    }

                    double speed = this.getSpeed();

                    //TODO
                    //this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), HunterSounds.ITEM_BOOMERANG_HIT, SoundCategory.BLOCKS, 0.5F, 1.0F);
                    if (piercingLevel < 1 || entityHits >= piercingLevel || speed < 0.4F) {
                        returnToOwner = true;
                        this.totalHits += 1;
                    }

                    this.entityHits += 1;
                }
            }


            if (this.isReturning() && loyaltyLevel < 1 && this.totalHits >= 6) {
                this.drop(this.getPosX(), this.getPosY(), this.getPosZ());
                return;
            }

            if (returnToOwner && !isReturning()) {
                if (this.getShooter() != null && this.shouldReturnToThrower() && EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, this.getBoomerang()) > 0) {
                    Entity shooter = this.getShooter();
                    this.world.playSound(null, shooter.func_233580_cy_(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);

                    Vector3d returnVec = this.throwPos.subtract(this.getPosX(), this.getPosY(), this.getPosZ()).normalize();
                    double velocity = this.getSpeed();

                    this.setMotion(velocity * returnVec.x, velocity * returnVec.y, velocity * returnVec.z);

                    this.markVelocityChanged();

                    this.setReturning(true);
                } else {
                    Vector3d returnVec = this.throwPos.subtract(this.getPosX(), this.getPosY(), this.getPosZ()).normalize();
                    double velocity = this.getSpeed();

                    this.setMotion(velocity * returnVec.x, velocity * returnVec.y, velocity * returnVec.z);

                    this.markVelocityChanged();

                    this.setReturning(true);
                }
            }
        }
    }

    private boolean shouldReturnToThrower() {
        Entity entity = this.getShooter();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        super.onCollideWithPlayer(entityIn);
        if (!this.world.isRemote && this.isReturning() && entityIn == this.getShooter()) {
            this.drop(this.getShooter().getPosX(), this.getShooter().getPosY(), this.getShooter().getPosZ());
        }
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        super.applyEntityCollision(entityIn);
        if (!this.world.isRemote && this.isReturning() && entityIn == this.getShooter()) {
            this.drop(this.getShooter().getPosX(), this.getShooter().getPosY(), this.getShooter().getPosZ());
        }
    }

    public void drop(double x, double y, double z) {

        if (!(this.getShooter() instanceof PlayerEntity) || (this.getShooter() instanceof PlayerEntity && !((PlayerEntity) this.getShooter()).isCreative())) {
            this.world.addEntity(new ItemEntity(this.world, x, y, z, this.getBoomerang().copy()));
        }

        this.remove();
    }

    public void shoot(Entity shooter, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(rotationYawIn * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float) Math.PI / 180F));
        float f1 = -MathHelper.sin(rotationPitchIn * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(rotationYawIn * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float) Math.PI / 180F));
        this.shoot((double) f, (double) f1, (double) f2, velocity, inaccuracy);
        this.setMotion(this.getMotion().add(shooter.getMotion().x, shooter.func_233570_aj_() ? 0.0D : shooter.getMotion().y, shooter.getMotion().z));
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vector3d vec3d = (new Vector3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy).scale((double) velocity);
        this.setMotion(vec3d);
        float f = MathHelper.sqrt(horizontalMag(vec3d));
        this.rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(vec3d.y, (double) f) * (double) (180F / (float) Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Override
    public void tick() {
        boolean flag = this.getNoClip();

        this.lastTickPosX = this.getPosX();
        this.lastTickPosY = this.getPosY();
        this.lastTickPosZ = this.getPosZ();
        super.tick();

        this.flyTick++;
        Vector3d vec3d = this.getMotion();

        Vector3d vec3d1 = this.getPositionVec();

        Vector3d vec3d2 = new Vector3d(this.getPosX() + this.getMotion().getX(), this.getPosY() + this.getMotion().getY(), this.getPosZ() + this.getMotion().getZ());

        RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));

        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vec3d1 = raytraceresult.getHitVec();
        }

        EntityRayTraceResult entityRaytraceResult = rayTraceEntities(vec3d1, vec3d2);


        if (entityRaytraceResult != null) {
            raytraceresult = entityRaytraceResult;
        }

        if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult) raytraceresult).getEntity();
            Entity entity1 = this.getShooter();
            if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity) entity1).canAttackPlayer((PlayerEntity) entity)) {
                raytraceresult = null;
            }
        }

        if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && this.world.getBlockState(this.func_233580_cy_()).getBlock() == Blocks.NETHER_PORTAL) {
            this.setPortal(this.func_233580_cy_());
        }
        if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.isAirBorne = true;
        }

        BlockRayTraceResult fluidRaytraceResult = this.world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, this));

        this.onHitFluid(fluidRaytraceResult);
        BoomerangEventFactory.boomerangSpeedTick(this, vec3d);


        double d2 = this.getPosX() + vec3d.x;
        double d3 = this.getPosY() + vec3d.y;
        double d4 = this.getPosZ() + vec3d.z;

        this.setPosition(d2, d3, d4);

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

        double f1 = 0.99;
        double f2 = this.getGravityVelocity();
        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() - this.getMotion().getX() * 0.25D, this.getPosY() - this.getMotion().getY() * 0.25D, this.getPosZ() - this.getMotion().getZ() * 0.25D, this.getMotion().getX(), this.getMotion().getY(), this.getMotion().getZ());
            }

            f1 = 0.8;
        }

        int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);

        Entity entity = this.getShooter();

        if (loyaltyLevel > 0 && !this.isReturning() && this.flyTick == 100) {
            if (entity != null) {
                this.world.playSound(null, entity.func_233580_cy_(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);
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
        } else if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0F, -f2, 0.0F));
            this.setMotion(this.getMotion().scale(f1));

        }

        this.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());

        this.collideWithNearbyEntities();
    }

    @Nullable
    protected EntityRayTraceResult rayTraceEntities(Vector3d p_213866_1_, Vector3d p_213866_2_) {
        return ProjectileHelper.rayTraceEntities(this.world, this, p_213866_1_, p_213866_2_, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (p_213871_1_) -> {
            return !p_213871_1_.isSpectator() && p_213871_1_.isAlive() && p_213871_1_.canBeCollidedWith() && (p_213871_1_ != this.getShooter());
        });
    }

    public boolean getNoClip() {
        if (!this.world.isRemote) {
            return this.noClip;
        } else {
            return false;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith();
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
        if (this.shootingEntity != null) {
            nbt.putUniqueId("shootingEntityId", this.shootingEntity);
        }

        nbt.put("boomerang", this.getBoomerang().write(new CompoundNBT()));
        nbt.putDouble("throwX", this.throwPos.x);
        nbt.putDouble("throwY", this.throwPos.y);
        nbt.putDouble("throwZ", this.throwPos.z);
        nbt.putByte("entityHits", (byte) this.entityHits);
        nbt.putByte("totalHits", (byte) this.totalHits);
        nbt.putBoolean("returning", this.isReturning());
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        if (nbt.hasUniqueId("shootingEntityId")) {
            this.shootingEntity = nbt.getUniqueId("shootingEntityId");
        }

        this.setBoomerang(ItemStack.read(nbt.getCompound("boomerang")));
        this.throwPos = new Vector3d(nbt.getDouble("throwX"), nbt.getDouble("throwY"), nbt.getDouble("throwZ"));
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

                    if (entity == this.getShooter()) {
                        this.drop(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                    }
                }
            }
        }
    }

    private double getGravityVelocity() {
        return Math.min(1, Math.pow(2, -(Math.abs(this.getMotion().getX()) + Math.abs(this.getMotion().getZ())) * 2)) * 0.03;
    }

    public boolean isReturning() {
        return this.dataManager.get(RETURNING);
    }

    public void setShooter(@Nullable Entity entityIn) {
        this.shootingEntity = entityIn == null ? null : entityIn.getUniqueID();

    }

    @Nullable
    public Entity getShooter() {
        return this.shootingEntity != null && this.world instanceof ServerWorld ? ((ServerWorld) this.world).getEntityByUuid(this.shootingEntity) : null;
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