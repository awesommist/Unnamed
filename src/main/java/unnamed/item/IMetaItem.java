package unnamed.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public interface IMetaItem {
    IIcon getIcon();

    String getUnlocalizedName(ItemStack stack);

    boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player);

    boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ);

    ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player, World world);

    void registerIcons(IIconRegister register);

    void addRecipe();

    void addToCreativeList(Item item, int meta, List<ItemStack> result);

    boolean hasEffect(int renderPass);
}