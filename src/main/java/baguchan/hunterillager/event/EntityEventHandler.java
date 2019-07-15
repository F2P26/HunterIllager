package baguchan.hunterillager.event;

import baguchan.hunterillager.HunterEntityRegistry;
import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.world.DifficultyInstance;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityEventHandler {

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) event.getEntity();
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, EntityHunterIllager.class, 16.0F, 0.8D, 0.8D));
        }

        if (event.getEntity() instanceof PillagerEntity) {
            PillagerEntity pillager = (PillagerEntity) event.getEntity();

            if (pillager.func_213658_ej() && !pillager.isRaidActive()) {
                DifficultyInstance difficultyinstance = event.getWorld().getDifficultyForLocation(pillager.getPosition());

                int j = difficultyinstance.getDifficulty().getId();

                if (event.getWorld().rand.nextInt(18 - j) == 0) {
                    for (int i = 0; i < 2 + event.getWorld().rand.nextInt(1 + j); ++i) {
                        EntityHunterIllager entityHunterIllager = HunterEntityRegistry.HUNTERILLAGER.create(event.getWorld());

                        entityHunterIllager.setPosition((double) pillager.getPosition().getX() + 0.5D, (double) pillager.getPosition().getY() + 1.0D, (double) pillager.getPosition().getZ() + 0.5D);
                        entityHunterIllager.onInitialSpawn(event.getWorld(), event.getWorld().getDifficultyForLocation(pillager.getPosition()), SpawnReason.EVENT, null, null);
                        entityHunterIllager.onGround = true;
                        event.getWorld().addEntity(entityHunterIllager);
                    }
                }
            }
        }
    }

}
