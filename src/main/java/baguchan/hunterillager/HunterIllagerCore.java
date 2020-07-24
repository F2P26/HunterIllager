package baguchan.hunterillager;

import baguchan.hunterillager.client.HunterRenderingRegistry;
import baguchan.hunterillager.event.EntityEventHandler;
import baguchan.hunterillager.structure.FeatureRegister;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

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
        StructureSeparationSettings settings = new StructureSeparationSettings(26, 10, 14234632);

        // Register separation settings for big dungeon in the settings presets
        ImmutableSet.of(DimensionSettings.Preset.field_236122_b_, DimensionSettings.Preset.field_236123_c_, DimensionSettings.Preset.field_236124_d_,
                DimensionSettings.Preset.field_236125_e_, DimensionSettings.Preset.field_236126_f_, DimensionSettings.Preset.field_236127_g_)
                .forEach(p -> p.func_236137_b_().func_236108_a_().func_236195_a_().put(FeatureRegister.HUNTER_HOUSE_STRUCTURE, settings));

        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {

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
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        HunterRenderingRegistry.registerRenderers();
    }

}
