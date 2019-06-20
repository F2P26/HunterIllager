package baguchan.hunterillager;

import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class HunterEntityRegistry {
    public static final EntityType<EntityHunterIllager> HUNTERILLAGER = createEntity(EntityHunterIllager.class,EntityHunterIllager::new,EntityClassification.MONSTER,"hunterillager",0.6F, 1.95F);

    private static <T extends Entity> EntityType<T> createEntity(Class<T> entityClass, EntityType.IFactory<T> factory,EntityClassification entityClassification,String name, float width, float height) {
        ResourceLocation location = new ResourceLocation(HunterIllagerCore.MODID +":" + name);

        EntityType<T> entity = EntityType.Builder.create(factory, entityClassification).size(width, height).build(location.toString());
        entity.setRegistryName(location);

        return entity;
    }

    @SubscribeEvent
    public static void registerEntity(IForgeRegistry<EntityType<?>> event) {

        event.register(HUNTERILLAGER);

    }

    private static String prefix(String path) {

        return HunterIllagerCore.MODID + "." + path;

    }

}
