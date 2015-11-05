package unnamed.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import unnamed.api.IInventoryCallback;
import unnamed.utils.ItemUtils;

public class GenericInventory implements IInventory {

    public static final String TAG_SLOT = "Slot";
    public static final String TAG_ITEMS = "Items";
    public static final String TAG_SIZE = "size";

    protected List<IInventoryCallback> callbacks;
    protected String inventoryTitle;
    protected int slotsCount;
    protected ItemStack[] inventoryContents;
    protected boolean isInvNameLocalized;

    public GenericInventory(String name, boolean isInvNameLocalized, int size) {
        this.callbacks = new ArrayList<IInventoryCallback>();
        this.isInvNameLocalized = isInvNameLocalized;
        this.slotsCount = size;
        this.inventoryTitle = name;
        this.inventoryContents = new ItemStack[size];
    }

    public GenericInventory addCallback(IInventoryCallback callback) {
        callbacks.add(callback);
        return this;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (inventoryContents[slot] != null) {
            ItemStack itemstack;

            if (inventoryContents[slot].stackSize <= amount) {
                itemstack = inventoryContents[slot];
                inventoryContents[slot] = null;
                onInventoryChanged(slot);
                return itemstack;
            }
            itemstack = inventoryContents[slot].splitStack(amount);
            if (inventoryContents[slot].stackSize == 0) {
                inventoryContents[slot] = null;
            }

            onInventoryChanged(slot);
            return itemstack;
        }
        return null;
    }

    @Override
    public int getSizeInventory() {
        return slotsCount;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventoryContents[slot];
    }

    public ItemStack getStackInSlot(Enum<?> slot) {
        return getStackInSlot(slot.ordinal());
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot >= inventoryContents.length) return null;
        if (inventoryContents[slot] != null) {
            ItemStack itemstack = inventoryContents[slot];
            inventoryContents[slot] = null;
            return itemstack;
        }
        return null;
    }

    public boolean isItem(int slot, Item item) {
        return inventoryContents[slot] != null && inventoryContents[slot].getItem() == item;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    public void onInventoryChanged(int slot) {
        for (IInventoryCallback callback : callbacks)
            callback.onInventoryChanged(this, slot);
    }

    public void clearAndSetSlotCount(int amount) {
        slotsCount = amount;
        inventoryContents = new ItemStack[amount];
        onInventoryChanged(0);
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(TAG_SIZE)) {
            slotsCount = tag.getInteger(TAG_SIZE);
        }
        NBTTagList nbttaglist = tag.getTagList(TAG_ITEMS, 10);
        inventoryContents = new ItemStack[slotsCount];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound stacktag = nbttaglist.getCompoundTagAt(i);
            int j = stacktag.getByte(TAG_SLOT);
            if (j >= 0 && j < inventoryContents.length) {
                inventoryContents[j] = ItemUtils.readStack(stacktag);
            }
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        inventoryContents[i] = itemstack;

        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
            itemstack.stackSize = getInventoryStackLimit();

        onInventoryChanged(i);
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(TAG_SIZE, getSizeInventory());
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventoryContents.length; ++i) {
            if (inventoryContents[i] != null) {
                NBTTagCompound stacktag = ItemUtils.writeStack(inventoryContents[i]);
                stacktag.setByte(TAG_SLOT, (byte) i);
                nbttaglist.appendTag(stacktag);
            }
        }
        tag.setTag(TAG_ITEMS, nbttaglist);
    }

    /**
     * This bastard never even gets called, so don't rely on it.
     */
    @Override
    public void markDirty() {
        onInventoryChanged(0);
    }

    public void copyFrom(IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            if (i < getSizeInventory()) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null)
                    setInventorySlotContents(i, stack.copy());
                else
                    setInventorySlotContents(i, null);
            }
        }
    }

    public List<ItemStack> contents() {
        return Arrays.asList(inventoryContents);
    }

    @Override
    public String getInventoryName() {
        return inventoryTitle;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return isInvNameLocalized;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}
}