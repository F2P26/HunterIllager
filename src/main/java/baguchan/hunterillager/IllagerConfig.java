package baguchan.hunterillager;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("WeakerAccess")
@Config(modid = HunterIllagerCore.MODID)
@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID)
public class IllagerConfig {
    private final static String config = HunterIllagerCore.MODID + ".config.";

    @Config.LangKey(config + "woodhut_gen")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 500, max = 1000)
    @Config.Comment("WoodHut Rarity Given this value as X, 1 hut will spawn in X plain biome and wasteland biome chunks")
    public static int woodhutGen = 500;


    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(HunterIllagerCore.MODID)) {
            ConfigManager.sync(HunterIllagerCore.MODID, Config.Type.INSTANCE);
        }
    }
}