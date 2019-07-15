package baguchan.hunterillager.structure;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.IForgeRegistry;

public class FeatureRegister {
    public static final Structure<NoFeatureConfig> HUNTER_HOUSE = new HunterHouseStructure(NoFeatureConfig::deserialize);

    public static final IStructurePieceType HUNTER_HOUSE_STRUCTURE = IStructurePieceType.register(HunterHousePieces.Piece::new, "HunterHouse");


    public static void registerStructure(IForgeRegistry<Feature<?>> registry) {
        registry.register(HUNTER_HOUSE.setRegistryName("hanter_housae"));
    }
}
