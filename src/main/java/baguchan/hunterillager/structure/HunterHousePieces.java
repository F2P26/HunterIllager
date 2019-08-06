package baguchan.hunterillager.structure;

import baguchan.hunterillager.HunterIllagerCore;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;

public class HunterHousePieces {
    public static void addStructure(ChunkGenerator<?> p_215139_0_, TemplateManager templateManagerIn, BlockPos p_215139_2_, List<StructurePiece> p_215139_3_, SharedSeedRandom p_215139_4_) {
        JigsawManager.func_214889_a(new ResourceLocation(HunterIllagerCore.MODID,"illager_campbase"), 7, HunterHousePieces.HunterHouse::new, p_215139_0_, templateManagerIn, p_215139_2_, p_215139_3_, p_215139_4_);
    }


    public static class HunterHouse extends AbstractVillagePiece {
        public HunterHouse(TemplateManager p_i50560_1_, JigsawPiece p_i50560_2_, BlockPos p_i50560_3_, int p_i50560_4_, Rotation p_i50560_5_, MutableBoundingBox p_i50560_6_) {
            super(FeatureRegister.HUNTER_HOUSE_STRUCTURE, p_i50560_1_, p_i50560_2_, p_i50560_3_, p_i50560_4_, p_i50560_5_, p_i50560_6_);
        }

        public HunterHouse(TemplateManager p_i50561_1_, CompoundNBT p_i50561_2_) {
            super(p_i50561_1_, p_i50561_2_, FeatureRegister.HUNTER_HOUSE_STRUCTURE);
        }
    }
}