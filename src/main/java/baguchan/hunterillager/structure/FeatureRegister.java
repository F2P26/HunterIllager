package baguchan.hunterillager.structure;

import baguchan.hunterillager.HunterIllagerCore;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;

public class FeatureRegister {
    public static final Structure<NoFeatureConfig> HUNTER_HOUSE = new HunterHouseStructure(NoFeatureConfig::deserialize);

    public static final IStructurePieceType HUNTER_HOUSE_STRUCTURE = IStructurePieceType.register(HunterHousePieces.HunterHouse::new, "HHS");


    public static void registerStructure(IForgeRegistry<Feature<?>> registry) {
        registry.register(HUNTER_HOUSE.setRegistryName(HunterIllagerCore.MODID,"hunterhouse"));

        JigsawManager.field_214891_a.register(new JigsawPattern(new ResourceLocation(HunterIllagerCore.MODID, "illager_campbase"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SingleJigsawPiece(HunterIllagerCore.MODID + ":" + "illager_campbase"), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawManager.field_214891_a.register(new JigsawPattern(new ResourceLocation(HunterIllagerCore.MODID, "feature_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SingleJigsawPiece(HunterIllagerCore.MODID + ":" + "illager_woodhut"), 1), Pair.of(new SingleJigsawPiece(HunterIllagerCore.MODID + ":" + "illager_farmhut"), 1), Pair.of(new SingleJigsawPiece(HunterIllagerCore.MODID + ":" + "illager_barrelhut"), 1), Pair.of(new SingleJigsawPiece(HunterIllagerCore.MODID + ":" + "illager_lamp"), 1)), JigsawPattern.PlacementBehaviour.RIGID));

        Registry.register(Registry.STRUCTURE_FEATURE,"HunterHouse".toLowerCase(Locale.ROOT), HUNTER_HOUSE);
        Registry.register(Registry.STRUCTURE_PIECE,"HHS".toLowerCase(Locale.ROOT), HUNTER_HOUSE_STRUCTURE);
        Feature.STRUCTURES.put("HunterHouse".toLowerCase(Locale.ROOT), HUNTER_HOUSE);
    }
}
