package baguchan.hunterillager;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HunterConfig {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static boolean generateHunterHouse;
    public static boolean generateVariantHunterHouse;

    static {
        Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void bakeConfig() {
        generateHunterHouse = COMMON.generateHunterHouse.get();
        generateVariantHunterHouse = COMMON.generateVariantHunterHouse.get();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == HunterConfig.COMMON_SPEC) {
            bakeConfig();
        }
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue generateHunterHouse;
        public final ForgeConfigSpec.BooleanValue generateVariantHunterHouse;

        public Common(ForgeConfigSpec.Builder builder) {
            generateHunterHouse = builder
                    .translation(HunterIllagerCore.MODID + ".config.generateHunterHouse")
                    .define("generate HunterHouse", true);

            generateVariantHunterHouse = builder
                    .translation(HunterIllagerCore.MODID + ".config.generateVariantHunterHouse")
                    .define("generate Variant HunterHouse", true);
        }
    }

}