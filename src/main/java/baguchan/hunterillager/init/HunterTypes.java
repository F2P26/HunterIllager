package baguchan.hunterillager.init;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.huntertype.HunterType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HunterTypes {

    public static final HunterType PLAIN = new HunterType(new HunterType.Properties());
    public static final HunterType SNOW = new HunterType(new HunterType.Properties());


    private static ForgeRegistry<HunterType> registry;

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = (ForgeRegistry<HunterType>) new RegistryBuilder<HunterType>()
                .setType(HunterType.class)
                .setName(new ResourceLocation(HunterIllagerCore.MODID, "hunter_type"))
                .setDefaultKey(new ResourceLocation(HunterIllagerCore.MODID, "plain"))
                .create();
    }


    @SubscribeEvent
    public static void onRegisterHunterType(RegistryEvent.Register<HunterType> event) {
        event.getRegistry().registerAll(PLAIN.setRegistryName("plain"),
                SNOW.setRegistryName("snow"));
    }

    public static ForgeRegistry<HunterType> getRegistry() {
        if (registry == null) {
            throw new IllegalStateException("Registry not yet initialized");
        }
        return registry;
    }

    public static int getId(HunterType biome) {
        return registry.getID(biome);
    }

    public static HunterType byId(int id) {
        return registry.getValue(id);
    }
}