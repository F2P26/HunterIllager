package baguchan.hunterillager;

import baguchan.hunterillager.client.HunterRenderingRegistry;
import baguchan.hunterillager.item.HunterItems;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

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
    }



    private void setup(final FMLCommonSetupEvent event) {
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

}
