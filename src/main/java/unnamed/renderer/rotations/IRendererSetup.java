package unnamed.renderer.rotations;

import net.minecraft.client.renderer.RenderBlocks;
import unnamed.geometry.Orientation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IRendererSetup {
    @SideOnly(Side.CLIENT)
    RenderBlocks enter(Orientation orientation, int metadata, RenderBlocks renderer);

    @SideOnly(Side.CLIENT)
    void exit(RenderBlocks renderer);
}