package thaumcraft.api.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * 
 * @author azanor
 *
 * Custom tile entity class I use for most of my tile entities. Setup in such a way that only 
 * the nbt data within readCustomNBT / writeCustomNBT will be sent to the client when the tile
 * updates. Apart from all the normal TE data that gets sent that is.
 * 
 */
public class TileThaumcraft extends TileEntity {
		
	//NBT stuff
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        readCustomNBT(nbt);
    }
	
	public void readCustomNBT(NBTTagCompound nbt)
    {
        //TODO
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        writeCustomNBT(nbt);
		return nbt;
    }
	
	public void writeCustomNBT(NBTTagCompound nbt)
    {
		//TODO
    }
	
	//Client Packet stuff
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), -999,  this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeCustomNBT(nbt);
		return nbt;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);		
		this.readCustomNBT(pkt.getNbtCompound());
	}
	
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
	public EnumFacing getFacing() {
		try {
			return EnumFacing.getFront(getBlockMetadata() & 7);
		} catch (Exception e) {	}
		return EnumFacing.UP;
	}
	
	public boolean gettingPower() {
		return worldObj.isBlockPowered(getPos());
	}

}
