package unnamed.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import unnamed.Log;

public final class TextureUtils {

    public final static int TEXTURE_MAP_BLOCKS = 0;
    public final static int TEXTURE_MAP_ITEMS = 1;

    public static void bindItemStackTexture(ItemStack stack) {
        bindIconSheet(stack.getItemSpriteNumber());
    }

    public static void bindIconSheet(int type) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            TextureManager manager = mc.getTextureManager();
            bindIconSheet(manager, type);
        } else {
            Log.warn("Binding texture to null client");
        }
    }

    public static void bindIconSheet(TextureManager manager, int type) {
        final ResourceLocation resourceLocation = manager.getResourceLocation(type);
        manager.bindTexture(resourceLocation);
    }

    public static void bindTextureToClient(ResourceLocation texture) {
        if (texture != null) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc != null)
                mc.renderEngine.bindTexture(texture);
            else
                Log.warn("Binding texture to null client");
        } else {
            Log.warn("Texture resource location can't be null");
        }
    }

    public static void bindDefaultTerrainTexture() {
        bindTextureToClient(TextureMap.locationBlocksTexture);
    }

    public static void bindDefaultItemsTexture() {
        bindTextureToClient(TextureMap.locationItemsTexture);
    }
}