package unnamed.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class UnnamedBlock extends Block implements IRegisterableBlock {

    public static final int UNNAMED_TE_GUI = -1;

    protected UnnamedBlock(Material material) {
        super (material);

        isBlockContainer = false;
    }

    protected abstract Object getModInstance();
}