package unnamed.gui.misc;

import java.util.Set;

import net.minecraftforge.common.util.ForgeDirection;
import unnamed.api.IValueProvider;
import unnamed.api.IValueReceiver;
import unnamed.utils.bitmap.IWriteableBitMap;

public interface IConfigurableGuiSlots<T extends Enum<T>> {
    IValueProvider<Set<ForgeDirection>> createAllowedDirectionsProvider(T slot);

    IWriteableBitMap<ForgeDirection> createAllowedDirectionsReceiver(T slot);

    IValueProvider<Boolean> createAutoFlagProvider(T slot);

    IValueReceiver<Boolean> createAutoSlotReceiver(T slot);
}