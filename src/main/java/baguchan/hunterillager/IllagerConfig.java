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
    @Config.Comment("WoodHut Rarity Given this value as X, 1 hut will spawn in X plain type biome chunks")
    public static int woodhutGen = 540;

    @Config.LangKey(config + "animal_ranaway")
    @Config.RequiresMcRestart
    @Config.Comment("When this is true, HunterIllager use Dark Oak theme on WoodHut")
    public static boolean animal_RanAway = true;

    @Config.LangKey(config + "illager_use_darkoak_theme")
    @Config.RequiresMcRestart
    @Config.Comment("When this is true, HunterIllager ")
    public static boolean darkoak_theme = false;


    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(HunterIllagerCore.MODID)) {
            ConfigManager.sync(HunterIllagerCore.MODID, Config.Type.INSTANCE);
        }
    }
}