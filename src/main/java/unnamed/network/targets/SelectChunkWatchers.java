package unnamed.network.targets;

import java.util.Collection;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import unnamed.network.IPacketTargetSelector;
import unnamed.utils.NetUtils;
import unnamed.utils.coord.DimCoord;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;

public class SelectChunkWatchers implements IPacketTargetSelector {

    public static final IPacketTargetSelector INSTANCE = new SelectChunkWatchers();

    @Override
    public boolean isAllowedOnSide(Side side) {
        return side == Side.SERVER;
    }

    @Override
    public void listDispatchers(Object arg, Collection<NetworkDispatcher> result) {
        Preconditions.checkArgument(arg instanceof DimCoord, "Argument must be DimCoord");
        DimCoord coord = (DimCoord) arg;

        WorldServer server = DimensionManager.getWorld(coord.d);

        Set<EntityPlayerMP> players = NetUtils.getPlayersWatchingBlock(server, coord.x, coord.z);

        for (EntityPlayerMP player : players) {
            NetworkDispatcher dispatcher = NetUtils.getPlayerDispatcher(player);
            result.add(dispatcher);
        }
    }
}