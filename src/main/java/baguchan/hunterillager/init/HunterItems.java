package baguchan.hunterillager.init;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.item.BoomerangItem;
import baguchan.hunterillager.item.QuiverItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HunterItems {
    public static final Item SPAWNEGG_HUNTERILLAGER = new SpawnEggItem(HunterEntityRegistry.HUNTERILLAGER,  9804699, 0x582827, new Item.Properties().group(ItemGroup.MISC));
    public static final Item BOOMERANG = new BoomerangItem(new Item.Properties().group(ItemGroup.COMBAT).maxDamage(384));
    public static final Item QUIVER = new QuiverItem(new Item.Properties().group(ItemGroup.COMBAT));


    public static void register(IForgeRegistry<Item> registry, Item item, String id) {
        item.setRegistryName(new ResourceLocation(HunterIllagerCore.MODID, id));
        registry.register(item);
    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> registry) {
        register(registry.getRegistry(), SPAWNEGG_HUNTERILLAGER, "spawnegg_hunterillager");
        register(registry.getRegistry(), BOOMERANG, "boomerang");
    }

}