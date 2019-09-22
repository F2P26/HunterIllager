package baguchan.hunterillager.entity.projectile;

import baguchan.hunterillager.HunterEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class BoomerangEntity extends Entity implements IProjectile {
    private static final DataParameter<Boolean> RETURNING = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CANDROP = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<ItemStack> BOOMERANG = EntityDataManager.createKey(BoomerangEntity.class, DataSerializers.ITEMSTACK);

    @Nullable
    private UUID shootingEntity;
    private Vec3d throwPos;
    private int hits;

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, World world) {
        super(type, world);
    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, World world, @Nullable LivingEntity shootingEntity, ItemStack boomerang) {
        super(type, world);
        this.setShooter(shootingEntity);
        this.setBoomerang(boomerang);
        this.throwPos = new Vec3d(shootingEntity == null ? 0 : shootingEntity.posX, shootingEntity == null ? 0 : shootingEntity.posY + shootingEntity.getEyeHeight() - 0.1, shootingEntity == null ? 0 : shootingEntity.posZ);
        this.setPosition(this.throwPos.x, this.throwPos.y, this.throwPos.z);
        this.hits = 0;
    }

    public BoomerangEntity(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(HunterEntityRegistry.BOOMERANG, world);
    }

    public BoomerangEntity(World world, LivingEntity entity, ItemStack boomerang) {
        this(HunterEntityRegistry.BOOMERANG, world, entity, boomerang);
    }

    private void onHit(RayTraceResult result) {
        if (!this.world.isRemote) {
            boolean returnToOwner = result.getType() == RayTraceResult.Type.BLOCK;

            if (result.getType() == RayTraceResult.Type.BLOCK && ((BlockRayTraceResult) result).getPos() != null) {
                BlockPos pos = ((BlockRayTraceResult) result).getPos();
                BlockState state = this.world.getBlockState(pos);
                SoundType soundType = state.getSoundType(this.world, pos, this);
                this.world.playSound(null, this.posX, this.posY, this.posZ, soundType.getHitSound(), SoundCategory.BLOCKS, soundType.getVolume() * 0.26f, soundType.getPitch());
                //this.world.playSound(null, this.posX, this.posY, this.posZ, HunterSounds.ITEM_BOOMERANG_HIT, SoundCategory.BLOCKS, 0.5F, 1.0F);
                this.hits++;
            }

            if (result.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) result).getEntity() instanceof LivingEntity && ((EntityRayTraceResult) result).getEntity() != this.getShooter()) {
                Entity shooter = this.getShooter();
                int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, this.getBoomerang());
                ((LivingEntity) ((EntityRayTraceResult) result).getEntity()).attackEntityFrom(DamageSource.causeThrownDamage(this, shooter), (float) (2 * Math.sqrt(this.getMotion().getX() * this.getMotion().getX() + this.getMotion().getZ() * this.getMotion().getZ()) + Math.min(1, sharpness) + Math.max(0, sharpness - 1) * 0.5));

                if (shooter instanceof LivingEntity) {
                    this.getBoomerang().damageItem(1, (LivingEntity) shooter, (p_222182_1_) -> {
                    });
                }

                //TODO
                //this.world.playSound(null, this.posX, this.posY, this.posZ, HunterSounds.ITEM_BOOMERANG_HIT, SoundCategory.BLOCKS, 0.5F, 1.0F);

                returnToOwner = true;
                this.hits += 3;
            }

            if (this.isReturning() && this.hits >= 6) {
                this.drop(this.posX, this.posY, this.posZ);
                return;
            }

            if (returnToOwner) {
                if (this.getShooter() != null && EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, this.getBoomerang()) > 0) {
                    Entity shooter = this.getShooter();
                    this.world.playSound(null, shooter.getPosition(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 1);
                    this.drop(shooter.posX, shooter.posY, shooter.posZ);
                } else {
                    Vec3d returnVec = this.throwPos.subtract(this.posX, this.posY, this.posZ).normalize();
                    double velocity = this.getVelocity();

                    this.setMotion(velocity * returnVec.x, -velocity * returnVec.y, velocity * returnVec.z);

                    this.markVelocityChanged();

                    this.setReturning(true);
                }
            }
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        super.onCollideWithPlayer(entityIn);
        if (!this.world.isRemote && this.isReturning() && entityIn == this.getShooter()) {
            this.drop(this.getShooter().posX, this.getShooter().posY, this.getShooter().posZ);
        }
    }

    public void drop(double x, double y, double z) {
        if (this.isCanDrop()) {
            if (!(this.getShooter() instanceof PlayerEntity) || (this.getShooter() instanceof PlayerEntity && !((PlayerEntity) this.getShooter()).isCreative())) {
                this.world.addEntity(new ItemEntity(this.world, x, y, z, this.getBoomerang().copy()));
            }
        }
        this.remove();
    }

    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(rotationYawIn * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float) Math.PI / 180F));
        float f1 = MathHelper.sin((rotationPitchIn + pitchOffset) * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(rotationYawIn * ((float) Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float) Math.PI / 180F));
        this.shoot((double) f, (double) f1, (double) f2, velocity, inaccuracy);
        this.getMotion().add(entityThrower.getMotion().getX(), 0.0F, entityThrower.getMotion().getZ());

        if (!entityThrower.onGround) {
            this.getMotion().add(0.0F, entityThrower.getMotion().getY(), 0.0F);
        }
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        x = x + this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy;
        y = y + this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy;
        z = z + this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy;
        x = x * (double) velocity;
        y = y * (double) velocity;
        z = z * (double) velocity;

        this.setMotion(x, y, z);

        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float) (MathHelper.atan2(x, z) * (double) (180F / (float) Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(y, (double) f1) * (double) (180F / (float) Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Override
    public void tick() {
        boolean flag = this.func_203047_q();

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.tick();

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);

        Vec3d vec3d1 = new Vec3d(this.posX + this.getMotion().getX(), this.posY + this.getMotion().getY(), this.posZ + this.getMotion().getZ());

        RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));

        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vec3d1 = raytraceresult.getHitVec();
        }

        EntityRayTraceResult entityRaytraceResult = func_213866_a(vec3d, vec3d1);


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

        if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && this.world.getBlockState(this.getPosition()).getBlock() == Blocks.NETHER_PORTAL) {
            this.setPortal(this.getPosition());
        }
        if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
        }


        this.posX += this.getMotion().getX();
        this.posY -= this.getMotion().getY();
        this.posZ += this.getMotion().getZ();
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
                this.world.addParticle(ParticleTypes.BUBBLE, this.posX - this.getMotion().getX() * 0.25D, this.posY - this.getMotion().getY() * 0.25D, this.posZ - this.getMotion().getZ() * 0.25D, this.getMotion().getX(), this.getMotion().getY(), this.getMotion().getZ());
            }

            f1 = 0.8;
        }

        if (this.isReturning()) {
            f2 *= 0.5;
        }

        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0F, f2, 0.0F));
        }
        this.setMotion(this.getMotion().scale(f1));

        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Nullable
    protected EntityRayTraceResult func_213866_a(Vec3d p_213866_1_, Vec3d p_213866_2_) {
        return ProjectileHelper.func_221271_a(this.world, this, p_213866_1_, p_213866_2_, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (p_213871_1_) -> {
            return !p_213871_1_.isSpectator() && p_213871_1_.isAlive() && p_213871_1_.canBeCollidedWith() && (p_213871_1_ != this.getShooter());
        });
    }

    public boolean func_203047_q() {
        if (!this.world.isRemote) {
            return this.noClip;
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
        this.dataManager.register(RETURNING, false);
        this.dataManager.register(CANDROP, false);
        this.dataManager.register(BOOMERANG, ItemStack.EMPTY);
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        if (this.shootingEntity != null) {
            nbt.putUniqueId("shootingEntityId", this.shootingEntity);
        }

        nbt.put("boomerang", this.getBoomerang().write(new CompoundNBT()));
        nbt.putBoolean("canDrop", this.isCanDrop());
        nbt.putDouble("throwX", this.throwPos.x);
        nbt.putDouble("throwY", this.throwPos.y);
        nbt.putDouble("throwZ", this.throwPos.z);
        nbt.putByte("hits", (byte) this.hits);
        nbt.putBoolean("returning", this.isReturning());
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        if (nbt.hasUniqueId("shootingEntityId")) {
            this.shootingEntity = nbt.getUniqueId("shootingEntityId");
        }

        this.setBoomerang(ItemStack.read(nbt.getCompound("boomerang")));
        this.setCandrop(nbt.getBoolean("canDrop"));
        this.throwPos = new Vec3d(nbt.getDouble("throwX"), nbt.getDouble("throwY"), nbt.getDouble("throwZ"));
        this.hits = nbt.getByte("hits");
        this.setReturning(nbt.getBoolean("returning"));
    }

    private double getGravityVelocity() {
        return Math.min(1, Math.pow(2, -(Math.abs(this.getMotion().getX()) + Math.abs(this.getMotion().getZ())) * 2)) * 0.03;
    }

    public boolean isReturning() {
        return this.dataManager.get(RETURNING);
    }

    public boolean isCanDrop() {
        return this.dataManager.get(CANDROP);
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

    public double getVelocity() {
        return Math.sqrt(this.getMotion().getX() * this.getMotion().getX() + this.getMotion().getY() * this.getMotion().getY() + this.getMotion().getZ() * this.getMotion().getZ());
    }

    public void setCandrop(boolean candrop) {
        this.dataManager.set(CANDROP, candrop);
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