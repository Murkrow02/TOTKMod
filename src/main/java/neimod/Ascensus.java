package neimod;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Ascensus extends Item {
    public Ascensus(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {

        //Find first air airBlock above player
        var airBlockY = FindSurfaceY(world, playerEntity);

        //If no air airBlock was found, return fail
        if(airBlockY == null){
            return TypedActionResult.fail(playerEntity.getStackInHand(hand));
        }

        //Air Block is found, tp player to it
        playerEntity.teleport(playerEntity.getX(), airBlockY, playerEntity.getZ());
        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }

    final int MAX_HEIGHT = 300;
    final int MAX_BLOCKS_TO_SEARCH = 256;
    private Integer FindSurfaceY(World world, PlayerEntity player){

        //Get player coordinates
        int x = (int)player.getX();
        int y = (int)player.getY();
        int z = (int)player.getZ();

        //Skip air above player
        y = SkipAirAbove(world, x, y, z);
        return GetFirstAirBlock(world, x, y, z);
    }

    @Nullable
    private Integer GetFirstAirBlock(World world, int x, int y, int z) {
        //Count to prevent too many airBlock for search
        int blocksSearched = 0;

        //Loop until air airBlock is found or max height is reached
        boolean foundAir = false;
        while(!foundAir && blocksSearched < MAX_BLOCKS_TO_SEARCH && y < MAX_HEIGHT ){

            //Searched one more airBlock
            blocksSearched++;

            //Get type of airBlock at +1 y
            var currentBlock = world.getBlockState(new BlockPos(x, ++y, z)).getBlock();

            //FOUND!
            if(currentBlock == Blocks.AIR){
                foundAir = true;
                return y;
            }
        }
        return null;
    }

    private int SkipAirAbove(World world, int x, int y, int z) {
        //If player has already air on top of his head, skip these block before searching for new air
        var firstBlockAbovePlayer = world.getBlockState(new BlockPos(x, y +1, z)).getBlock();
        if(firstBlockAbovePlayer == Blocks.AIR){
            while(firstBlockAbovePlayer == Blocks.AIR && y < MAX_HEIGHT){
                y++;
                firstBlockAbovePlayer = world.getBlockState(new BlockPos(x, y +1, z)).getBlock();
            }
        }
        return y;
    }
}
