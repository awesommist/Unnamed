package unnamed.config.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import unnamed.item.ItemUnnamedBlock;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterBlock {

    String DEFAULT = "[default]";
    String NONE = "[none]";

    @interface RegisterTileEntity {
        String name();

        Class<? extends TileEntity> cls();
    }

    String name();

    Class<? extends ItemBlock> itemBlock() default ItemUnnamedBlock.class;

    Class<? extends TileEntity> tileEntity() default TileEntity.class;

    RegisterTileEntity[] tileEntities() default {};

    String unlocalizedName() default DEFAULT;

    boolean isEnabled() default true;

    boolean isConfigurable() default true;
}