package baguchan.hunterillager.structure;

import baguchan.hunterillager.HunterIllagerCore;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class HunterHousePieces {
    private static final BlockPos STRUCTURE_OFFSET = new BlockPos(4, 0, 15);

    private static final ResourceLocation[] sturcuture_resoucelocate = new ResourceLocation[]{new ResourceLocation(HunterIllagerCore.MODID,"illager_capmbase"), new ResourceLocation(HunterIllagerCore.MODID,"illager_woodhut")};

  /*  public static void func_204760_a(TemplateManager p_204760_0_, BlockPos p_204760_1_, Rotation p_204760_2_, List<StructurePiece> p_204760_3_, Random p_204760_4_) {
        ResourceLocation resourcelocation = sturcuture_resoucelocate[p_204760_4_.nextInt(sturcuture_resoucelocate.length)];
        p_204760_3_.add(new HunterHousePieces.Piece(p_204760_0_, resourcelocation, p_204760_1_, p_204760_2_));
    }*/

    public static void func_204041_a(TemplateManager p_204041_0_, BlockPos p_204041_1_, Rotation p_204041_2_, List<StructurePiece> p_204041_3_, Random p_204041_4_) {
        boolean flag = p_204041_4_.nextFloat() <= 0.5F;
        float f = flag ? 0.9F : 0.8F;
        func_204045_a(p_204041_0_, p_204041_1_, p_204041_2_, p_204041_3_, p_204041_4_, flag, f);
        if (flag && p_204041_4_.nextFloat() <= 0.85F) {
            func_204047_a(p_204041_0_, p_204041_4_, p_204041_2_, p_204041_1_, p_204041_3_);
        }

    }

    private static void func_204045_a(TemplateManager manager, BlockPos pos, Rotation rotation, List<StructurePiece> p_204045_3_, Random random, boolean p_204045_6_, float p_204045_7_) {

        ResourceLocation resourcelocation = sturcuture_resoucelocate[random.nextInt(sturcuture_resoucelocate.length)];
        p_204045_3_.add(new HunterHousePieces.Piece(manager, resourcelocation, pos, rotation));
    }

    private static void func_204047_a(TemplateManager p_204047_0_, Random p_204047_1_, Rotation p_204047_2_, BlockPos p_204047_3_, List<StructurePiece> p_204047_5_) {
        int i = p_204047_3_.getX();
        int j = p_204047_3_.getZ();
        BlockPos blockpos = Template.getTransformedPos(new BlockPos(15, 0, 15), Mirror.NONE, p_204047_2_, BlockPos.ZERO).add(i, 0, j);
        MutableBoundingBox mutableboundingbox = MutableBoundingBox.createProper(i, 0, j, blockpos.getX(), 0, blockpos.getZ());
        BlockPos blockpos1 = new BlockPos(Math.min(i, blockpos.getX()), 0, Math.min(j, blockpos.getZ()));
        List<BlockPos> list = func_204044_a(p_204047_1_, blockpos1.getX(), blockpos1.getZ());
        int k = MathHelper.nextInt(p_204047_1_, 4, 8);

        for(int l = 0; l < k; ++l) {
            if (!list.isEmpty()) {
                int i1 = p_204047_1_.nextInt(list.size());
                BlockPos blockpos2 = list.remove(i1);
                int j1 = blockpos2.getX();
                int k1 = blockpos2.getZ();
                Rotation rotation = Rotation.values()[p_204047_1_.nextInt(Rotation.values().length)];
                BlockPos blockpos3 = Template.getTransformedPos(new BlockPos(5, 0, 6), Mirror.NONE, rotation, BlockPos.ZERO).add(j1, 0, k1);
                MutableBoundingBox mutableboundingbox1 = MutableBoundingBox.createProper(j1, 0, k1, blockpos3.getX(), 0, blockpos3.getZ());
                if (!mutableboundingbox1.intersectsWith(mutableboundingbox)) {
                    func_204045_a(p_204047_0_, blockpos2, rotation, p_204047_5_, p_204047_1_, false, 0.8F);
                }
            }
        }

    }

    private static List<BlockPos> func_204044_a(Random p_204044_0_, int p_204044_1_, int p_204044_2_) {
        List<BlockPos> list = Lists.newArrayList();
        list.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7)));
        list.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ + MathHelper.nextInt(p_204044_0_, 1, 7)));
        list.add(new BlockPos(p_204044_1_ - 16 + MathHelper.nextInt(p_204044_0_, 1, 8), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 8)));
        list.add(new BlockPos(p_204044_1_ + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7)));
        list.add(new BlockPos(p_204044_1_ + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 6)));
        list.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + 16 + MathHelper.nextInt(p_204044_0_, 3, 8)));
        list.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ + MathHelper.nextInt(p_204044_0_, 1, 7)));
        list.add(new BlockPos(p_204044_1_ + 16 + MathHelper.nextInt(p_204044_0_, 1, 7), 90, p_204044_2_ - 16 + MathHelper.nextInt(p_204044_0_, 4, 8)));
        return list;
    }

    public static class Piece extends TemplateStructurePiece {
        private final Rotation rotation;
        private final ResourceLocation field_204756_e;

        public Piece(TemplateManager p_i48904_1_, ResourceLocation p_i48904_2_, BlockPos p_i48904_3_, Rotation p_i48904_4_) {
            super(FeatureRegister.HUNTER_HOUSE_STRUCTURE, 0);
            this.templatePosition = p_i48904_3_;
            this.rotation = p_i48904_4_;
            this.field_204756_e = p_i48904_2_;
            this.func_204754_a(p_i48904_1_);
        }

        public Piece(TemplateManager p_i50445_1_, CompoundNBT p_i50445_2_) {
            super(FeatureRegister.HUNTER_HOUSE_STRUCTURE, p_i50445_2_);
            this.field_204756_e = new ResourceLocation(p_i50445_2_.getString("Template"));
            this.rotation = Rotation.valueOf(p_i50445_2_.getString("Rot"));
            this.func_204754_a(p_i50445_1_);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("Template", this.field_204756_e.toString());
            tagCompound.putString("Rot", this.rotation.name());
        }

        private void func_204754_a(TemplateManager p_204754_1_) {
            Template template = p_204754_1_.getTemplateDefaulted(this.field_204756_e);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setCenterOffset(HunterHousePieces.STRUCTURE_OFFSET).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
            this.setup(template, this.templatePosition, placementsettings);
        }

        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
         * the end, it adds Fences...
         */
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            BlockPos blockpos1 = this.templatePosition.add(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1);

            int i = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
            BlockPos blockpos2 = this.templatePosition;
            this.templatePosition = this.templatePosition.add(0, i - 90, 0);
            boolean flag = super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, p_74875_4_);

            this.templatePosition = blockpos2;
            return flag;
        }
    }
}