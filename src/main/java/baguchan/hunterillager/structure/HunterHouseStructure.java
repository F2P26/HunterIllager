package baguchan.hunterillager.structure;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.MarginedStructureStart;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;
import java.util.function.Function;

public class HunterHouseStructure extends Structure<NoFeatureConfig> {
    public HunterHouseStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51440_1_) {
        super(p_i51440_1_);
    }

    protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
        int i = this.getBiomeFeatureDistance(chunkGenerator);
        int j = this.getBiomeFeatureSeparation(chunkGenerator);
        int k = x + i * spacingOffsetsX;
        int l = z + i * spacingOffsetsZ;
        int i1 = k < 0 ? k - i + 1 : k;
        int j1 = l < 0 ? l - i + 1 : l;
        int k1 = i1 / i;
        int l1 = j1 / i;
        ((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, this.getSeedModifier());
        k1 = k1 * i;
        l1 = l1 * i;
        k1 = k1 + random.nextInt(i - j);
        l1 = l1 + random.nextInt(i - j);
        return new ChunkPos(k1, l1);
    }

    public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
        ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
        if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
            Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos(chunkPosX * 16 + 9, 0, chunkPosZ * 16 + 9));
            if (chunkGen.hasStructure(biome, FeatureRegister.HUNTER_HOUSE)) {
                for (int k = chunkPosX - 10; k <= chunkPosX + 10; ++k) {
                    for (int l = chunkPosZ - 10; l <= chunkPosZ + 10; ++l) {
                        if (Feature.VILLAGE.hasStartAt(chunkGen, rand, k, l)) {
                            return false;
                        }
                    }
                }

                return true;
            }
        }

        return false;

    }

    public String getStructureName() {
        return "HunterHouse";
    }

    public int getSize() {
        return 3;
    }

    public Structure.IStartFactory getStartFactory() {
        return HunterHouseStructure.Start::new;
    }

    protected int getSeedModifier() {
        return 10387313;
    }

    protected int getBiomeFeatureDistance(ChunkGenerator<?> chunkGenerator) {
        return 20;
    }

    protected int getBiomeFeatureSeparation(ChunkGenerator<?> chunkGenerator) {
        return 8;
    }

    public static class Start extends MarginedStructureStart {
        public Start(Structure<?> p_i50460_1_, int p_i50460_2_, int p_i50460_3_, Biome p_i50460_4_, MutableBoundingBox p_i50460_5_, int p_i50460_6_, long p_i50460_7_) {
            super(p_i50460_1_, p_i50460_2_, p_i50460_3_, p_i50460_4_, p_i50460_5_, p_i50460_6_, p_i50460_7_);
        }

        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            BlockPos blockpos = new BlockPos(chunkX * 16, 90, chunkZ * 16);

            Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
            HunterHousePieces.addStructure(templateManagerIn, blockpos, rotation, this.components, this.rand);
            this.recalculateStructureSize();
        }
    }
}