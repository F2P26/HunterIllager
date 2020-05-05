package baguchan.hunterillager.structure;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.entity.HunterIllagerEntity;
import baguchan.hunterillager.init.HunterEntityRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.BiomeDictionary;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class HunterHousePieces {
    private static final ResourceLocation crafthut = new ResourceLocation(HunterIllagerCore.MODID, "illager_crafthouse");

    private static final ResourceLocation hunterbase_Template = new ResourceLocation(HunterIllagerCore.MODID, "illager_woodhut");
    private static final ResourceLocation snowny_hunterbase_Template = new ResourceLocation(HunterIllagerCore.MODID, "illager_woodhut_snow");

    private static final Map<ResourceLocation, BlockPos> structurePos = ImmutableMap.of(crafthut, new BlockPos(12, 0, 8), hunterbase_Template, BlockPos.ZERO, snowny_hunterbase_Template, BlockPos.ZERO);

    public static void addStructure(TemplateManager p_207617_0_, BlockPos p_207617_1_, Rotation p_207617_2_, List<StructurePiece> p_207617_3_, Random p_207617_4_, Biome biome) {

        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
            p_207617_3_.add(new HunterHousePieces.Piece(p_207617_0_, snowny_hunterbase_Template, p_207617_1_, p_207617_2_, 0));
        } else {
            p_207617_3_.add(new HunterHousePieces.Piece(p_207617_0_, hunterbase_Template, p_207617_1_, p_207617_2_, 0));
        }

        //p_207617_3_.add(new HunterHousePieces.Piece(p_207617_0_, crafthut, p_207617_1_, p_207617_2_, 0));
    }


    public static class Piece extends TemplateStructurePiece {
        private final ResourceLocation field_207615_d;
        private final Rotation field_207616_e;

        public Piece(TemplateManager p_i49313_1_, ResourceLocation p_i49313_2_, BlockPos p_i49313_3_, Rotation p_i49313_4_, int p_i49313_5_) {
            super(FeatureRegister.HUNTER_HOUSE_STRUCTURE, 0);
            this.field_207615_d = p_i49313_2_;
            BlockPos blockpos = HunterHousePieces.structurePos.get(p_i49313_2_);
            this.templatePosition = p_i49313_3_.add(blockpos.getX(), blockpos.getY() - p_i49313_5_, blockpos.getZ());
            this.field_207616_e = p_i49313_4_;
            this.func_207614_a(p_i49313_1_);
        }

        public Piece(TemplateManager p_i50566_1_, CompoundNBT p_i50566_2_) {
            super(FeatureRegister.HUNTER_HOUSE_STRUCTURE, p_i50566_2_);
            this.field_207615_d = new ResourceLocation(p_i50566_2_.getString("Template"));
            this.field_207616_e = Rotation.valueOf(p_i50566_2_.getString("Rot"));
            this.func_207614_a(p_i50566_1_);
        }

        private void func_207614_a(TemplateManager p_207614_1_) {
            Template template = p_207614_1_.getTemplateDefaulted(this.field_207615_d);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            this.setup(template, this.templatePosition, placementsettings);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("Template", this.field_207615_d.toString());
            tagCompound.putString("Rot", this.field_207616_e.name());
        }

        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
            if ("hunter".equals(function)) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                HunterIllagerEntity hunterIllager = HunterEntityRegistry.HUNTERILLAGER.create(worldIn.getWorld());
                hunterIllager.enablePersistence();
                hunterIllager.setPosition((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
                hunterIllager.setMainHome(pos);
                hunterIllager.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(pos), SpawnReason.STRUCTURE, (ILivingEntityData) null, (CompoundNBT) null);
                worldIn.addEntity(hunterIllager);
            }
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
         * the end, it adds Fences...
         */
        public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            BlockPos blockpos = this.template.getSize();

            BlockPos blockpos1 = this.templatePosition;
            int i = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
            BlockPos blockpos2 = this.templatePosition;
            this.templatePosition = this.templatePosition.add(0, i - 90 - 1, 0);
            boolean flag = super.func_225577_a_(worldIn, chunkGenerator, randomIn, structureBoundingBoxIn, p_74875_4_);


            this.templatePosition = blockpos2;
            return flag;
        }
    }
}