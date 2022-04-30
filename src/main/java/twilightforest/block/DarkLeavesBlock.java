package twilightforest.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DarkLeavesBlock extends TFLeavesBlock {

	protected DarkLeavesBlock(Properties props) {
		super(props);
	}

    @Override
	public int getFlammability() {
		return 1;
	}

	@Override
	public int getFireSpreadSpeed() {
		return 0;
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return Shapes.block();
	}
	
	@Override
	public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return 15;
	}
}
