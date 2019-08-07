package baguchan.hunterillager.structure;

import baguchan.hunterillager.HunterIllagerCore;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;

public class FeatureRegister {
    public static final Structure<NoFeatureConfig> HUNTER_HOUSE = new HunterHouseStructure(NoFeatureConfig::deserialize);

    public static final IStructurePieceType HUNTER_HOUSE_STRUCTURE = IStructurePieceType.register(HunterHousePieces.Piece::new, "HHS");


    public static void registerStructure(IForgeRegistry<Feature<?>> registry) {
        registry.register(HUNTER_HOUSE.setRegistryName(HunterIllagerCore.MODID,"hunterhouse"));
        Registry.register(Registry.STRUCTURE_FEATURE,"HunterHouse".toLowerCase(Locale.ROOT), HUNTER_HOUSE);
        Registry.register(Registry.STRUCTURE_PIECE,"HHS".toLowerCase(Locale.ROOT), HUNTER_HOUSE_STRUCTURE);
        Feature.STRUCTURES.put("HunterHouse".toLowerCase(Locale.ROOT), HUNTER_HOUSE);
    }
}
