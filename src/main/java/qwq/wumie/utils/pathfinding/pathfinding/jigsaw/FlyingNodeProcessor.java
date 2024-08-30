package qwq.wumie.utils.pathfinding.pathfinding.jigsaw;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import static meteordevelopment.meteorclient.utils.world.BlockInfo.*;
public class FlyingNodeProcessor extends NodeProcessor {

	@Override
	public void process(Node currentNode, Node endNode) {
		for(Node neighbor : getNeighbors(currentNode)) {
			boolean vclipableDown = false;
			boolean vclipableUp = false;
			if((!neighbor.isWalkable() && !((vclipableDown = neighbor.isVclipableDown()) || (vclipableUp = neighbor.isVclipableUp()))) || isNodeClosed(neighbor)) {
				continue;
			}
			if(vclipableDown) {
				neighbor = createNode(neighbor.getBlockpos().up());
			}
			if(vclipableUp) {
				neighbor = createNode(neighbor.getBlockpos().down());
			}

			double tentativeGCost = currentNode.gCost + (Node.distance(currentNode.getBlockpos(), neighbor.getBlockpos())) * getGCostMultiplier(neighbor.getBlockpos());

			if(isNodeOpen(neighbor)) {
				if(neighbor.gCost > tentativeGCost) {
					neighbor.gCost = tentativeGCost;
				}
			}
			else {
				neighbor.gCost = tentativeGCost;
				openNode(neighbor);
			}

			neighbor.hCost = Node.distance(neighbor.getBlockpos(), endNode.getBlockpos());
			neighbor.fCost = neighbor.gCost + neighbor.hCost;
			neighbor.parent = currentNode;
		}
	}

	@Override
	public Node createNode(BlockPos pos) {
		Node node = new Node(!isStairBlock(getBlock(pos.down())) && isPassable(getBlockState(pos),pos) && isPassable(getBlockState(pos.up()),pos.up()), pos).setId(pos.hashCode());

		node.setVclipableDown(isVclipableDown(node));
		node.setVclipableUp(isVclipableUp(node));
		return node;
	}

	@Override
	public boolean isPassable(BlockState blockState,BlockPos pos) {
		return blockState.isAir() || (blockState.getBlock() instanceof PlantBlock && !(blockState.getBlock() instanceof LilyPadBlock)) || blockState.getBlock() instanceof VineBlock
				|| blockState.getFluidState().isOf(Fluids.WATER) ||
				blockState.getBlock() instanceof SignBlock || blockState.getBlock() instanceof WallSignBlock || blockState.getBlock() instanceof LadderBlock || blockState.getBlock() instanceof AbstractRedstoneGateBlock;
	}

	@Override
	public boolean isWalkable(BlockState block,BlockPos pos) {
		return !(block.isAir()) && (block.getBlock() instanceof LilyPadBlock || block.getBlock() instanceof VineBlock || block.getBlock() instanceof LadderBlock || block.isFullCube(MinecraftClient.getInstance().world, pos));
	}

	public boolean isVclipableDown(Node node) {
		if(!node.isWalkable()) {
			return false;
		}
		BlockPos blockPos = node.getBlockpos();
		if(isPassable(getBlockState(blockPos.down()),blockPos.down())) {
			return false;
		}
		for(int i = 1; i < 9; i++) {
			BlockPos pos = new BlockPos(blockPos.getX(), blockPos.getY() - i, blockPos.getZ());
			if(isPassable(getBlockState(pos),pos) && i < 8
					&& isPassable(getBlockState(pos.up()),pos.up())) {
				node.setVclipPosDown(pos);
				return true;
			}
		}
		return false;
	}

	public boolean isVclipableUp(Node node) {
		if(!node.isWalkable()) {
			return false;
		}
		BlockPos blockPos = node.getBlockpos();
		if(isPassable(getBlockState(blockPos.up()),blockPos.up())) {
			return false;
		}
		for(int i = 1; i < 9; i++) {
			BlockPos pos = new BlockPos(blockPos.getX(), blockPos.getY() + i, blockPos.getZ());
			if(isPassable(getBlockState(pos),pos) && i < 8
					&& isPassable(getBlockState(pos.up()),pos.up())) {
				node.setVclipPosUp(pos);
				return true;
			}
		}
		return false;
	}

}
