package baguchan.hunterillager.structure;

import baguchan.hunterillager.HunterIllagerCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = HunterIllagerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeatureRegister {
    public static final Structure<NoFeatureConfig> HUNTER_HOUSE_STRUCTURE = new HunterHouseStructure(NoFeatureConfig.field_236558_a_);

    public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> HUNTER_HOUSE = HUNTER_HOUSE_STRUCTURE.func_236391_a_(NoFeatureConfig.field_236559_b_);

    public static final IStructurePieceType HUNTER_HOUSE_STRUCTURE_PIECE = registerStructurePiece(new ResourceLocation(HunterIllagerCore.MODID, "hunter_house"), HunterHousePieces.Piece::new);

    public static <C extends IFeatureConfig> IStructurePieceType registerStructurePiece(ResourceLocation key, IStructurePieceType pieceType) {
        return Registry.register(Registry.STRUCTURE_PIECE, key, pieceType);
    }

    @SubscribeEvent
    public static void registerStructure(RegistryEvent.Register<Structure<?>> registry) {
        registry.getRegistry().register(HUNTER_HOUSE_STRUCTURE.setRegistryName(HunterIllagerCore.MODID, "hunterhouse"));
        Structure.field_236365_a_.put(prefix("hunterhouse"), HUNTER_HOUSE_STRUCTURE);
        //DimensionStructuresSettings.field_236191_b_.put(HUNTER_HOUSE_STRUCTURE,new StructureSeparationSettings(28, 6, 13257618));
    }


    public static void addFeaturesAndStructuresToBiomes(MutableRegistry<Biome> biomeReg, Biome biome, ResourceLocation biomeID, Map<String, List<String>> allBiomeBlacklists) {
        String biomeNamespace = biomeID.getNamespace();
        String biomePath = biomeID.getPath();

        if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND && (biomeNamespace.equals("minecraft") || biomeNamespace.equals("biomesoplenty")))
            biome.func_242440_e().func_242487_a().add(() -> HUNTER_HOUSE_STRUCTURE.func_236391_a_(IFeatureConfig.NO_FEATURE_CONFIG));
    }

    private static String prefix(String path) {
        return HunterIllagerCore.MODID + ":" + path;
    }
}
