package qwq.wumie.utils.pathfinding.pathfinding.jigsaw;


import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import static meteordevelopment.meteorclient.utils.world.BlockInfo.*;
public class WalkingNodeProcessor extends NodeProcessor {

	@Override
	public Node createNode(BlockPos pos) {
		return new Node(isWalkable(getBlockState(pos.down()),pos.down()) && isPassable(getBlockState(pos),pos) && isPassable(getBlockState(pos.up()),pos.up()), pos).setId(pos.hashCode());
	}

	@Override
	public boolean isPassable(BlockState blockState,BlockPos pos) {
		return blockState.isAir() || (blockState.getBlock() instanceof PlantBlock && !(blockState.getBlock() instanceof LilyPadBlock)) || blockState.getBlock() instanceof VineBlock
				|| blockState.getFluidState().isOf(Fluids.WATER)|| blockState.getBlock() instanceof AbstractRedstoneGateBlock ||
				blockState.getBlock() instanceof SignBlock || blockState.getBlock() instanceof WallSignBlock || blockState.getBlock() instanceof LadderBlock;
	}

	@Override
	public boolean isWalkable(BlockState blockState,BlockPos pos) {
		MinecraftClient mc = MinecraftClient.getInstance();
		return !blockState.isAir() && (blockState.getBlock() instanceof LilyPadBlock || blockState.getBlock() instanceof LadderBlock || blockState.isFullCube(mc.world,pos));
	}

}
