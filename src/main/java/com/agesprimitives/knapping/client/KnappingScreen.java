package com.agesprimitives.knapping.client;

import com.agesprimitives.knapping.menu.KnappingMenu;
import com.agesprimitives.network.C2SKnappingClickPacket;
import com.agesprimitives.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

public class KnappingScreen extends AbstractContainerScreen<KnappingMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("agesprimitives", "textures/gui/knapping.png");
    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 256;
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 186;
    private static final int CELL_SIZE = 14;
    private static final int GRID_X = 20;
    private static final int GRID_Y = 20;
    private static final int RESULT_X = KnappingMenu.RESULT_SLOT_X;
    private static final int RESULT_Y = KnappingMenu.RESULT_SLOT_Y;
    private static final int ARROW_X = 97;
    private static final int ARROW_Y = 46;
    private static final int ARROW_W = 22;
    private static final int ARROW_H = 15;
    private boolean draggingKnapping;
    private int lastDraggedIndex = -1;

    public KnappingScreen(KnappingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = 86;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        int localMouseX = mouseX - left;
        int localMouseY = mouseY - top;

        graphics.blit(TEXTURE, left, top, 0, 0, GUI_WIDTH, GUI_HEIGHT, TEX_WIDTH, TEX_HEIGHT);

        int gridWidthPx = menu.getGridWidth() * CELL_SIZE;
        int gridHeightPx = menu.getGridHeight() * CELL_SIZE;

        graphics.fill(left + GRID_X - 2, top + GRID_Y - 2, left + GRID_X + gridWidthPx + 2, top + GRID_Y + gridHeightPx + 2, 0xFF1B1611);

        if (hasResource(menu.getActiveCellsTexture())) {
            graphics.blit(menu.getActiveCellsTexture(), left + GRID_X, top + GRID_Y, 0, 0, gridWidthPx, gridHeightPx, gridWidthPx, gridHeightPx);
        }

        for (int y = 0; y < menu.getGridHeight(); y++) {
            for (int x = 0; x < menu.getGridWidth(); x++) {
                int x0 = left + GRID_X + x * CELL_SIZE;
                int y0 = top + GRID_Y + y * CELL_SIZE;
                int u = x * CELL_SIZE;
                int v = y * CELL_SIZE;
                int texWidth = menu.getGridWidth() * CELL_SIZE;
                int texHeight = menu.getGridHeight() * CELL_SIZE;
                if (menu.isRemoved(x, y)) {
                    if (menu.useDisabledTexture() && hasResource(menu.getDisabledCellsTexture())) {
                        graphics.blit(menu.getDisabledCellsTexture(), x0, y0, u, v, CELL_SIZE, CELL_SIZE, texWidth, texHeight);
                    } else {
                        graphics.fill(x0, y0, x0 + CELL_SIZE, y0 + CELL_SIZE, 0xFF16120D);
                    }
                } else {
                    if (!hasResource(menu.getActiveCellsTexture())) {
                        graphics.fill(x0, y0, x0 + CELL_SIZE, y0 + CELL_SIZE, 0xFFA18A5D);
                    }
                }
            }
        }

        int hoveredX = getGridX(localMouseX);
        int hoveredY = getGridY(localMouseY);
        if (hoveredX >= 0 && hoveredY >= 0) {
            int hx0 = left + GRID_X + hoveredX * CELL_SIZE;
            int hy0 = top + GRID_Y + hoveredY * CELL_SIZE;
            graphics.fill(hx0, hy0, hx0 + CELL_SIZE, hy0 + CELL_SIZE, 0x80FFFFFF);
        }

        if (localMouseX >= RESULT_X && localMouseX < RESULT_X + 16 && localMouseY >= RESULT_Y && localMouseY < RESULT_Y + 16) {
            graphics.fill(left + RESULT_X, top + RESULT_Y, left + RESULT_X + 16, top + RESULT_Y + 16, 0x80FFFFFF);
        }

    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(font, Component.translatable("screen.agesprimitives.knapping.hint"), leftPos + 4, topPos - 10, 0xFFFFFF, true);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int localX = (int) mouseX - leftPos;
        int localY = (int) mouseY - topPos;

        if (button == 0) {
            if (isArrowHovered(localX, localY) && openRecipeViewer()) {
                return true;
            }

            if (trySendGridClick(localX, localY)) {
                draggingKnapping = true;
                return true;
            }

        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && draggingKnapping) {
            int localX = (int) mouseX - leftPos;
            int localY = (int) mouseY - topPos;
            if (trySendGridClick(localX, localY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingKnapping = false;
            lastDraggedIndex = -1;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean trySendGridClick(int localX, int localY) {
        int x = getGridX(localX);
        int y = getGridY(localY);
        if (x < 0 || y < 0) {
            return false;
        }

        int index = y * menu.getGridWidth() + x;
        if (lastDraggedIndex == index) {
            return true;
        }

        PacketHandler.CHANNEL.sendToServer(new C2SKnappingClickPacket(x, y));
        lastDraggedIndex = index;
        return true;
    }

    private int getGridX(int localX) {
        int gridPixelWidth = menu.getGridWidth() * CELL_SIZE;
        if (localX < GRID_X || localX >= GRID_X + gridPixelWidth) {
            return -1;
        }
        return (localX - GRID_X) / CELL_SIZE;
    }

    private int getGridY(int localY) {
        int gridPixelHeight = menu.getGridHeight() * CELL_SIZE;
        if (localY < GRID_Y || localY >= GRID_Y + gridPixelHeight) {
            return -1;
        }
        return (localY - GRID_Y) / CELL_SIZE;
    }

    private boolean isArrowHovered(int localX, int localY) {
        return localX >= ARROW_X && localX < ARROW_X + ARROW_W && localY >= ARROW_Y && localY < ARROW_Y + ARROW_H;
    }

    private boolean openRecipeViewer() {
        ResourceLocation knappingTypeId = menu.getKnappingTypeId();
        if (ModList.get().isLoaded("jei") && invokeViewer("com.agesprimitives.integration.jei.AgesPrimitivesJeiPlugin", knappingTypeId)) {
            return true;
        }
        return ModList.get().isLoaded("emi")
                && invokeViewer("com.agesprimitives.integration.emi.AgesPrimitivesEmiPlugin", knappingTypeId);
    }

    private static boolean invokeViewer(String className, ResourceLocation knappingTypeId) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod("openKnappingRecipes", ResourceLocation.class);
            Object result = method.invoke(null, knappingTypeId);
            return result instanceof Boolean b && b;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean hasResource(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }
}
