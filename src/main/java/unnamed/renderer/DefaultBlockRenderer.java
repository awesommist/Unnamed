package unnamed.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import unnamed.Log;
import unnamed.block.UnnamedBlock;
import unnamed.geometry.Orientation;
import unnamed.renderer.rotations.IRendererSetup;
import unnamed.tileentity.UnnamedTileEntity;
import unnamed.utils.CachedFactory;
import unnamed.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class DefaultBlockRenderer implements IBlockRenderer<Block> {

    private final CachedFactory<UnnamedBlock, TileEntity> inventoryTileEntities = new CachedFactory<UnnamedBlock, TileEntity>() {
        @Override
        protected TileEntity create(UnnamedBlock key) {
            return key.createTileEntityForRender();
        }
    };

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        if (!(block instanceof UnnamedBlock)) {
            RenderUtils.renderInventoryBlock(renderer, block, 0);
            return;
        }

        final UnnamedBlock unnamedBlock = (UnnamedBlock)block;

        try {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            if (unnamedBlock.shouldRenderTesrInInventory()) {
                TileEntity te = inventoryTileEntities.getOrCreate(unnamedBlock);
                if (te instanceof UnnamedTileEntity) ((UnnamedTileEntity) te).prepareForInventoryRender(block, metadata);
                GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
                GL11.glPushMatrix();
                GL11.glTranslated(-0.5, -0.5, -0.5);
                TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
                GL11.glPopMatrix();
                GL11.glPopAttrib();
            }

            if (unnamedBlock.shouldRenderBlock()) {
                final Orientation orientation = unnamedBlock.getInventoryRenderOrientation();
                unnamedBlock.setBoundsBasedOnOrientation(orientation);

                final int renderMetadata = unnamedBlock.getInventoryRenderMetadata(metadata);

                final IRendererSetup setup = unnamedBlock.getRotationMode().getRenderSetup();
                final RenderBlocks localRenderer = setup.enter(orientation, renderMetadata, renderer);

                RenderUtils.renderInventoryBlock(localRenderer, block, renderMetadata);
                setup.exit(localRenderer);
            }
        } catch (Exception e) {
            Log.severe(e, "Error during block '%s' rendering", block.getUnlocalizedName());
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof UnnamedBlock)) return renderer.renderStandardBlock(block, x, y, z);

        final UnnamedBlock unnamedBlock = (UnnamedBlock) block;

        if (unnamedBlock.shouldRenderBlock()) {
            final int metadata = world.getBlockMetadata(x, y, z);
            final IRendererSetup setup = unnamedBlock.getRotationMode().getRenderSetup();

            final Orientation orientation = unnamedBlock.getOrientation(metadata);
            final RenderBlocks localRenderer = setup.enter(orientation, metadata, renderer);
            boolean wasRendered = localRenderer.renderStandardBlock(unnamedBlock, x, y, z);
            setup.exit(localRenderer);
            return wasRendered;
        }
        return false;
    }

}