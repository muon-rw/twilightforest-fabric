package twilightforest.block;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twilightforest.client.ModelRegisterCallback;
import twilightforest.client.ModelUtils;
import twilightforest.entity.EntityTFSlideBlock;
import twilightforest.item.TFItems;

import java.util.Random;

public class BlockTFSlider extends RotatedPillarBlock {

	public static final IntegerProperty DELAY = IntegerProperty.create("delay", 0, 3);

	private static final int TICK_TIME = 80;
	private static final int OFFSET_TIME = 20;
	private static final int PLAYER_RANGE = 32;
	private static final float BLOCK_DAMAGE = 5;
	private static final AxisAlignedBB Y_BB = new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 1F, 0.6875);
	private static final AxisAlignedBB Z_BB = new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 1F);
	private static final AxisAlignedBB X_BB = new AxisAlignedBB(0, 0.3125, 0.3125, 1F, 0.6875, 0.6875);

	protected BlockTFSlider() {
		super(Properties.create(Material.IRON, MaterialColor.DIRT).hardnessAndResistance(2.0F, 10.0F));
		//this.setCreativeTab(TFItems.creativeTab); TODO 1.14
		this.setDefaultState(stateContainer.getBaseState().with(AXIS, Direction.Axis.Y).with(DELAY, 0));
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AXIS, DELAY);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return super.getMetaFromState(state) | state.getValue(DELAY);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).with(DELAY, meta & 0b11);
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
		switch (state.getValue(AXIS)) {
			case Y:
			default:
				return Y_BB;
			case X:
				return X_BB;
			case Z:
				return Z_BB;
		}
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random random) {
		if (!world.isRemote && this.isConnectedInRange(world, pos)) {
			//world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, TwilightForestMod.ID + ":random.creakstart", 0.75F, 1.5F);

			EntityTFSlideBlock slideBlock = new EntityTFSlideBlock(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state);
			world.addEntity(slideBlock);
		}

		scheduleBlockUpdate(world, pos);
	}

	/**
	 * Check if there is any players in range, and also recursively check connected blocks
	 */
	public boolean isConnectedInRange(World world, BlockPos pos) {
		Direction.Axis axis = world.getBlockState(pos).getValue(AXIS);

		switch (axis) {
			case Y:
				return this.anyPlayerInRange(world, pos) || this.isConnectedInRangeRecursive(world, pos, Direction.UP) || this.isConnectedInRangeRecursive(world, pos, Direction.DOWN);
			case X:
				return this.anyPlayerInRange(world, pos) || this.isConnectedInRangeRecursive(world, pos, Direction.WEST) || this.isConnectedInRangeRecursive(world, pos, Direction.EAST);
			case Z:
				return this.anyPlayerInRange(world, pos) || this.isConnectedInRangeRecursive(world, pos, Direction.NORTH) || this.isConnectedInRangeRecursive(world, pos, Direction.SOUTH);
			default:
				return this.anyPlayerInRange(world, pos);
		}
	}

	private boolean isConnectedInRangeRecursive(World world, BlockPos pos, Direction dir) {
		BlockPos dPos = pos.offset(dir);

		if (world.getBlockState(pos) == world.getBlockState(dPos)) {
			return this.anyPlayerInRange(world, dPos) || this.isConnectedInRangeRecursive(world, dPos, dir);
		} else {
			return false;
		}
	}

	private boolean anyPlayerInRange(World world, BlockPos pos) {
		return world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, PLAYER_RANGE, false) != null;
	}

	public void scheduleBlockUpdate(World world, BlockPos pos) {
		int offset = world.getBlockState(pos).getValue(DELAY);
		int update = TICK_TIME - ((int) (world.getWorldTime() - (offset * OFFSET_TIME)) % TICK_TIME);
		world.scheduleUpdate(pos, this, update);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, BlockState state) {
		scheduleBlockUpdate(world, pos);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		entity.attackEntityFrom(DamageSource.GENERIC, BLOCK_DAMAGE);
		if (entity instanceof EntityLivingBase) {
			double kx = (pos.getX() + 0.5 - entity.posX) * 2.0;
			double kz = (pos.getZ() + 0.5 - entity.posZ) * 2.0;

			((EntityLivingBase) entity).knockBack(null, 2, kx, kz);
		}
	}
}
