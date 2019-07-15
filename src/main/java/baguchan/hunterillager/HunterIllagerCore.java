package baguchan.hunterillager;

import baguchan.hunterillager.client.HunterRenderingRegistry;
import baguchan.hunterillager.event.EntityEventHandler;
import baguchan.hunterillager.item.HunterItems;
import baguchan.hunterillager.structure.FeatureRegister;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Set;

@Mod("hunterillager")
public class HunterIllagerCore
{
    public static final String MODID = "hunterillager";
    public static HunterIllagerCore instance;

    public HunterIllagerCore() {
        instance = this;
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::onItemsRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::onEntityRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, this::onFeatureRegistry);
    }



    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

        for (Biome biome : Biome.BIOMES) {


            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
            if (types.contains(BiomeDictionary.Type.FOREST)) {
                biome.addStructure(FeatureRegister.HUNTER_HOUSE, new NoFeatureConfig());
            }
        }
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
    public void onFeatureRegistry(final RegistryEvent.Register<Feature<?>> event) {
        IForgeRegistry<Feature<?>> registry = event.getRegistry();

        FeatureRegister.registerStructure(registry);
    }

}
