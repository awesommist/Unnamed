package unnamed.network.rpc.targets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import unnamed.network.rpc.IRpcTarget;
import unnamed.utils.WorldUtils;

public class TileEntityRpcTarget implements IRpcTarget {

    private TileEntity te;

    public TileEntityRpcTarget() {}

    public TileEntityRpcTarget(TileEntity te) {
        this.te = te;
    }

    @Override
    public Object getTarget() {
        return te;
    }

    @Override
    public void writeToStream(DataOutput output) throws IOException {
        output.writeInt(te.getWorldObj().provider.dimensionId);
        output.writeInt(te.xCoord);
        output.writeInt(te.yCoord);
        output.writeInt(te.zCoord);
    }

    @Override
    public void readFromStreamStream(EntityPlayer player, DataInput input) throws IOException {
        int worldId = input.readInt();
        int x = input.readInt();
        int y = input.readInt();
        int z = input.readInt();

        World world = WorldUtils.getWorld(worldId);
        te = world.getTileEntity(x, y, z);
    }

    @Override
    public void afterCall() {}
}