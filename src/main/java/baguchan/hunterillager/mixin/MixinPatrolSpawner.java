package baguchan.hunterillager.mixin;

import baguchan.hunterillager.HunterConfig;
import baguchan.hunterillager.init.HunterEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(PatrolSpawner.class)
public class MixinPatrolSpawner {

    @Inject(method = "spawnPatroller", at = @At("HEAD"), cancellable = true)
    private void spawnPatroller(ServerWorld worldIn, BlockPos p_222695_2_, Random random, boolean p_222695_4_, CallbackInfoReturnable<Boolean> callbackInfo) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!WorldEntitySpawner.func_234968_a_(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), EntityType.PILLAGER)) {
            callbackInfo.setReturnValue(false);
        } else if (!PatrollerEntity.func_223330_b(EntityType.PILLAGER, worldIn, SpawnReason.PATROL, p_222695_2_, random)) {
            callbackInfo.setReturnValue(false);
        } else {
            PatrollerEntity patrollerentity = EntityType.PILLAGER.create(worldIn);

            if (HunterConfig.spawnHunterIllagerOnPartrol && random.nextInt(3) == 0) {
                patrollerentity = HunterEntityRegistry.HUNTERILLAGER.create(worldIn);
            }

            if (patrollerentity != null) {
                if (p_222695_4_) {
                    patrollerentity.setLeader(true);
                    patrollerentity.resetPatrolTarget();
                }

                patrollerentity.setPosition((double) p_222695_2_.getX(), (double) p_222695_2_.getY(), (double) p_222695_2_.getZ());
                patrollerentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(p_222695_2_), SpawnReason.PATROL, (ILivingEntityData) null, (CompoundNBT) null);
                worldIn.func_242417_l(patrollerentity);
                callbackInfo.setReturnValue(true);
            } else {
                callbackInfo.setReturnValue(false);
            }
        }
    }
}
