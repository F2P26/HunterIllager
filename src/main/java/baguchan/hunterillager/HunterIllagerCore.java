package baguchan.hunterillager;

import baguchan.hunterillager.client.HunterRenderingRegistry;
import baguchan.hunterillager.event.EntityEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
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
    }


    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

        HunterIllagerCore.addFeatures();

    }

    private static void addFeatures() {
        /*for (Biome biome : ForgeRegistries.BIOMES.getValues()) {

            if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.VOID)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)
                    && (biome.getRegistryName().getNamespace().equals("minecraft")
                    || biome.getRegistryName().getNamespace().equals("biomesoplenty")
                    || biome.getRegistryName().getNamespace().equals("terraforged"))
                    && (BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLAINS)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST))
            ) {
                biome.func_235063_a_(FeatureRegister.HUNTER_HOUSE_STRUCTURE.func_236391_a_(NoFeatureConfig.field_236559_b_));
            }
        }*/
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        HunterRenderingRegistry.registerRenderers();
    }

}
