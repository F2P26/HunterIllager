package baguchan.hunterillager;

import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class HunterEntityRegistry {
    public static void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(HunterIllagerCore.MODID, "hunter_illager"), EntityHunterIllager.class, prefix("HunterIllager"), 1, HunterIllagerCore.instance, 90, 3, false, 9804699, 0x582827);

    }

    private static String prefix(String path) {

        return HunterIllagerCore.MODID + "." + path;

    }

}
