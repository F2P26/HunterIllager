package baguchan.hunterillager.event;

import baguchan.hunterillager.HunterConfig;
import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.structure.StructureRegister;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID)
public class BiomeEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addSpawn(BiomeLoadingEvent event) {
        if (HunterConfig.generateHunterHouse) {
            if (event.getName().getNamespace().toString().contains("minecraft") || event.getName().getNamespace().toString().contains("biomesoplenty")) {
                if (event.getCategory() == Biome.Category.FOREST || event.getCategory() == Biome.Category.PLAINS) {
                    event.getGeneration().withStructure(StructureRegister.HUNTER_HOUSE);
                }

            }

        }
    }
}
