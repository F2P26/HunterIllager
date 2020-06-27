package baguchan.hunterillager;

import baguchan.hunterillager.client.HunterRenderingRegistry;
import baguchan.hunterillager.event.EntityEventHandler;
import baguchan.hunterillager.init.HunterEntityRegistry;
import baguchan.hunterillager.init.HunterItems;
import baguchan.hunterillager.structure.FeatureRegister;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("hunterillager")
public class HunterIllagerCore {
    public static final String MODID = "hunterillager";
    public static HunterIllagerCore instance;

    public HunterIllagerCore() {
        instance = this;
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::onItemsRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::onEntityRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Structure.class, this::onStructureRegistry);
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

        DeferredWorkQueue.runLater(HunterIllagerCore::addFeatures);

    }

    private static void addFeatures() {
        ForgeRegistries.BIOMES.getValues().stream().forEach((biome -> {
            if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.VOID)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM)
                    && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)
                    && (biome.getRegistryName().getNamespace().equals("minecraft")
                    || biome.getRegistryName().getNamespace().equals("midnight")
                    || biome.getRegistryName().getNamespace().equals("biomesoplenty")
                    || biome.getRegistryName().getNamespace().equals("terraforged"))
                    && (BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLAINS)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST))
            ) {
                biome.func_235063_a_(FeatureRegister.HUNTER_HOUSE);
            }
        }));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        HunterRenderingRegistry.registerRenderers();
    }


    @SubscribeEvent
    public void onItemsRegistry(final RegistryEvent.Register<Item> event) {

        IForgeRegistry<Item> registry = event.getRegistry();

        HunterItems.registerItems(registry);

    }

    @SubscribeEvent
    public void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();
        HunterEntityRegistry.registerEntity(registry);
    }

    @SubscribeEvent
    public void onStructureRegistry(final RegistryEvent.Register<Structure<?>> event) {
        IForgeRegistry<Structure<?>> registry = event.getRegistry();

        FeatureRegister.registerStructure(registry);
    }

}
