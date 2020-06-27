package baguchan.hunterillager.structure;

import baguchan.hunterillager.HunterIllagerCore;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.IForgeRegistry;

public class FeatureRegister {
    public static final Structure<NoFeatureConfig> HUNTER_HOUSE_STRUCTURE = new HunterHouseStructure(NoFeatureConfig.field_236558_a_);

    public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> HUNTER_HOUSE = HUNTER_HOUSE_STRUCTURE.func_236391_a_(NoFeatureConfig.field_236559_b_);

    public static final IStructurePieceType HUNTER_HOUSE_STRUCTURE_PIECE = IStructurePieceType.register(HunterHousePieces.Piece::new, "HHS");


    public static void registerStructure(IForgeRegistry<Structure<?>> registry) {
        registry.register(HUNTER_HOUSE_STRUCTURE.setRegistryName(HunterIllagerCore.MODID, "hunterhouse"));
    }

}
