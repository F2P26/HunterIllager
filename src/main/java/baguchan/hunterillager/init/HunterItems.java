package baguchan.hunterillager.init;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.item.BoomerangItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class HunterItems {
    public static final Item SPAWNEGG_HUNTERILLAGER = new SpawnEggItem(HunterEntityRegistry.HUNTERILLAGER,  9804699, 0x582827, new Item.Properties().group(ItemGroup.MISC));
    public static final Item BOOMERANG = new BoomerangItem(new Item.Properties().group(ItemGroup.COMBAT).maxDamage(384));


    public static void register(IForgeRegistry<Item> registry, Item item, String id) {
        item.setRegistryName(new ResourceLocation(HunterIllagerCore.MODID, id));
        registry.register(item);
    }



    @SubscribeEvent
    public static void registerItems(IForgeRegistry<Item> registry) {
        register(registry, SPAWNEGG_HUNTERILLAGER, "spawnegg_hunterillager");
        register(registry, BOOMERANG, "boomerang");
    }

}