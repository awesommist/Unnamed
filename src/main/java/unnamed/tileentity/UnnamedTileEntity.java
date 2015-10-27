package unnamed.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import unnamed.Log;
import unnamed.api.IInventoryCallback;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class UnnamedTileEntity extends TileEntity {

    private boolean isUsedForClientInventoryRendering = false;

    public void setup() {}

    @SideOnly(Side.CLIENT)
    public void prepareForInventoryRender(Block block, int metadata) {
        if (worldObj != null) Log.severe("SEVERE PROGRAMMER ERROR! Inventory Render on World TileEntity. Expect hell!");
        isUsedForClientInventoryRendering = true;
        blockType = block;
        blockMetadata = metadata;
    }

    public boolean isRenderedInInventory() {
        return isUsedForClientInventoryRendering;
    }

    public boolean isAddedToWorld() {
        return worldObj != null;
    }

    public void markUpdated() {
        worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
    }

    protected IInventoryCallback createInventoryCallback() {
        return new IInventoryCallback() {
            @Override
            public void onInventoryChanged(IInventory inventory, int slotNumber) {
                markUpdated();
            }
        };
    }

    public boolean isValid(EntityPlayer player) {
        return (worldObj.getTileEntity(xCoord, yCoord, zCoord) == this) && (player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64.0D);
    }

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
        return oldBlock != newBlock;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", xCoord, yCoord, zCoord);
    }
}