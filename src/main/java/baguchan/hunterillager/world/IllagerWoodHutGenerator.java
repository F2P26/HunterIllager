package baguchan.hunterillager.world;

import baguchan.hunterillager.HunterIllagerCore;
import baguchan.hunterillager.IllagerConfig;
import baguchan.hunterillager.entity.EntityHunterIllager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class IllagerWoodHutGenerator implements IWorldGenerator {
    public static final ResourceLocation WOODHUT = new ResourceLocation(HunterIllagerCore.MODID, "illager_woodhut");

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!(world instanceof WorldServer))
            return;
        WorldServer sWorld = (WorldServer) world;

        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);

        BlockPos pos = getHeight(world, new BlockPos(x, 0, z));
        if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            if (BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.PLAINS)) {
                if (random.nextInt(IllagerConfig.woodhutGen) == 0) {

                    IBlockState state = world.getBlockState(pos.down());

                    pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

                    generateLabAt(sWorld, random, pos);

                    for (int i = 0; i < 2; i++) {
                        spawnIllager(world, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
                    }
                }
            }
        }
    }

    public static BlockPos getHeight(World world, BlockPos pos) {
        for (int y = 0; y < 256; y++) {
            BlockPos pos1 = pos.up(y);
            if (world.getBlockState(pos1.up()).getBlock() == Blocks.AIR && world.getBlockState(pos1.down()).getBlock() != Blocks.AIR) {

                return pos1;

            }
        }

        return pos;
    }

    protected boolean spawnIllager(World worldIn, int x, int y, int z) {

        EntityHunterIllager entityHunterIllager = new EntityHunterIllager(worldIn);
        entityHunterIllager.heal(entityHunterIllager.getMaxHealth());
        entityHunterIllager.setLocationAndAngles((double) x + 0.5D, (double) y, (double) z + 0.5D, 0.0F, 0.0F);
        entityHunterIllager.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityHunterIllager)), null);
        entityHunterIllager.enablePersistence();
        entityHunterIllager.setHomePosAndDistance(new BlockPos(x, y, z), 18);
        worldIn.spawnEntity(entityHunterIllager);
        return true;

    }

    public static void generateLabAt(WorldServer world, Random random, BlockPos pos) {
        MinecraftServer server = world.getMinecraftServer();
        Template template = world.getStructureTemplateManager().getTemplate(server, WOODHUT);
        PlacementSettings settings = new PlacementSettings();

        BlockPos size = template.getSize();
        for (int x = 0; x < size.getX(); x++)
            for (int y = 0; y < size.getY(); y++)
                for (int z = 0; z < size.getZ(); z++) {

                    template.addBlocksToWorld(world, pos, settings);
                }
    }
}