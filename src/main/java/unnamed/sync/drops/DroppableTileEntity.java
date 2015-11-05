package unnamed.sync.drops;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import unnamed.api.ICustomHarvestDrops;
import unnamed.api.ICustomPickItem;
import unnamed.api.IPlacerAwareTile;
import unnamed.tileentity.SyncedTileEntity;

public abstract class DroppableTileEntity extends SyncedTileEntity implements IPlacerAwareTile, ICustomHarvestDrops, ICustomPickItem {

    public DroppableTileEntity() {
        getDropSerializer().addFields(this);
    }

    @Override
    public boolean suppressNormalHarvestDrops() {
        return true;
    }

    protected ItemStack getRawDrop() {
        return new ItemStack(getBlockType());
    }

    @Override
    public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
        drops.add(getDropStack());
    }

    @Override
    public ItemStack getPickBlock() {
        return getDropStack();
    }

    protected ItemStack getDropStack() {
        return getDropSerializer().write(getRawDrop());
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase placer, ItemStack stack) {
        getDropSerializer().read(stack, true);
    }
}