package unnamed.utils.bitmap;

import net.minecraftforge.common.util.ForgeDirection;

public interface IRpcDirectionBitMap {
    void mark(ForgeDirection value);

    void clear(ForgeDirection value);

    void set(ForgeDirection key, boolean value);

    void toggle(ForgeDirection value);

    void clearAll();
}