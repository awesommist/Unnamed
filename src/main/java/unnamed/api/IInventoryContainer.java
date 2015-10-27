package unnamed.api;

import net.minecraft.inventory.IInventory;

public interface IInventoryContainer {
    IInventory[] getInternalInventories();
}