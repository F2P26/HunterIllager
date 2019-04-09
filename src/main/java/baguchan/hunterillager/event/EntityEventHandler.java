package baguchan.hunterillager.event;

import baguchan.hunterillager.IllagerConfig;
import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager) event.getEntity();
            villager.tasks.addTask(1, new EntityAIAvoidEntity<>(villager, EntityHunterIllager.class, 16.0F, 0.8D, 0.8D));
        }

        if(IllagerConfig.animal_RanAway) {
            if (!(event.getEntity() instanceof EntityTameable) && !(event.getEntity() instanceof EntityLlama) && event.getEntity() instanceof EntityAnimal) {
                EntityAnimal animal = (EntityAnimal) event.getEntity();
                animal.tasks.addTask(1, new EntityAIAvoidEntity<>(animal, EntityHunterIllager.class, 12.0F, 1.3D, 1.3D));
            }
        }
    }

}
