package unnamed.network.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

public interface IRpcTarget {
    Object getTarget();

    void writeToStream(DataOutput output) throws IOException;

    void readFromStreamStream(EntityPlayer player, DataInput input) throws IOException;

    void afterCall();
}