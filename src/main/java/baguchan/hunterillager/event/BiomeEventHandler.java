package baguchan.hunterillager.event;

import baguchan.hunterillager.HunterConfig;
import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.structure.StructureRegister;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID)
public class BiomeEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addSpawn(BiomeLoadingEvent event) {
        if (HunterConfig.generateHunterHouse) {
            event.getGeneration().withStructure(StructureRegister.HUNTER_HOUSE);
        }
    }
}
