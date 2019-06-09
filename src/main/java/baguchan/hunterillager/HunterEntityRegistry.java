package baguchan.hunterillager;

import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class HunterEntityRegistry {
    public static final EntityType<?> HUNTERILLAGER = EntityType.Builder.create(EntityHunterIllager::new, EntityClassification.CREATURE).size(0.6F, 1.95F).build(HunterIllagerCore.MODID + ":hunterillager").setRegistryName(HunterIllagerCore.MODID + ":hunterillager");

    @SubscribeEvent
    public static void registerEntity(IForgeRegistry<EntityType<?>> event) {

        event.register(HUNTERILLAGER);

    }

    private static String prefix(String path) {

        return HunterIllagerCore.MODID + "." + path;

    }

}
