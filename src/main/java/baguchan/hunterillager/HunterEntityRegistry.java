package baguchan.hunterillager;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class HunterEntityRegistry {
    public static final EntityType<HunterIllagerEntity> HUNTERILLAGER = EntityType.Builder.create(HunterIllagerEntity::new, EntityClassification.CREATURE).setTrackingRange(80).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).size(0.6F, 1.95F).build(prefix("hunterillager"));


    @SubscribeEvent
    public static void registerEntity(IForgeRegistry<EntityType<?>> event) {

        event.register(HUNTERILLAGER.setRegistryName("hunterillager"));

    }

    private static String prefix(String path) {

        return HunterIllagerCore.MODID + "." + path;

    }

}
