package unnamed.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemUnnamedBlock extends ItemBlock {

    public ItemUnnamedBlock(Block block) {
        super (block);
    }

    private static boolean canReplace(Block block, World world, int x, int y, int z) {
        return block != null && block.isReplaceable(world, x, y, z);
    }

    protected void afterBlockPlaced(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        stack.stackSize--;
    }

    protected boolean isStackValid(ItemStack stack, EntityPlayer player) {
        return stack.stackSize >= 0;
    }

    protected int calculateBlockMeta(World world, Block ownBlock, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int damage) {
        int newMeta = getMetadata(damage);
        return ownBlock.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, newMeta);
    }
}