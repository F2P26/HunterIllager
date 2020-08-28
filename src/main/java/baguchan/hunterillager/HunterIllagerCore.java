package baguchan.hunterillager;

import baguchan.hunterillager.client.HunterRenderingRegistry;
import baguchan.hunterillager.event.EntityEventHandler;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("hunterillager")
public class HunterIllagerCore {
    public static final String MODID = "hunterillager";
    public static HunterIllagerCore instance;

    public HunterIllagerCore() {
        instance = this;
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HunterConfig.COMMON_SPEC);
    }


    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

        HunterIllagerCore.addFeatures();

    }

    private static void addFeatures() {
        if (HunterConfig.generateHunterHouse) {
            StructureSeparationSettings settings = new StructureSeparationSettings(26, 10, 14234632);

            // Register separation settings
            ImmutableSet.of(DimensionSettings.field_242734_c, DimensionSettings.field_242735_d, DimensionSettings.field_242736_e,
                    DimensionSettings.field_242737_f, DimensionSettings.field_242738_g, DimensionSettings.field_242739_h)
                    .forEach(p ->);
            Registry.f
        }
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        HunterRenderingRegistry.registerRenderers();
    }

}
