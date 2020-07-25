package baguchan.hunterillager.init;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.entity.HunterIllagerEntity;
import baguchan.hunterillager.entity.projectile.BoomerangEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HunterEntityRegistry {
    public static final EntityType<HunterIllagerEntity> HUNTERILLAGER = EntityType.Builder.create(HunterIllagerEntity::new, EntityClassification.CREATURE).setTrackingRange(80).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).size(0.6F, 1.95F).build(prefix("hunterillager"));
    public static final EntityType<BoomerangEntity> BOOMERANG = EntityType.Builder.<BoomerangEntity>create(BoomerangEntity::new, EntityClassification.MISC).setTrackingRange(100).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(BoomerangEntity::new).size(0.4F, 0.2F).build(prefix("boomerang"));

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> event) {
        GlobalEntityTypeAttributes.put(HUNTERILLAGER, HunterIllagerEntity.getAttributeMap().func_233813_a_());

        event.getRegistry().register(HUNTERILLAGER.setRegistryName("hunterillager"));
        event.getRegistry().register(BOOMERANG.setRegistryName("boomerang"));

        Raid.WaveMember.create("hunterillager", HUNTERILLAGER, new int[]{0, 0, 1, 0, 1, 0, 1, 2});
    }

    private static String prefix(String path) {
        return HunterIllagerCore.MODID + "." + path;
    }

}
