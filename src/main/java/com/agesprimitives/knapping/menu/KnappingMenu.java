package com.agesprimitives.knapping.menu;

import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.knapping.KnappingRecipeIndex;
import com.agesprimitives.knapping.KnappingState;
import com.agesprimitives.knapping.KnappingType;
import com.agesprimitives.knapping.KnappingTypeManager;
import com.agesprimitives.network.PacketHandler;
import com.agesprimitives.network.S2CKnappingSyncPacket;
import com.agesprimitives.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;

public class KnappingMenu extends AbstractContainerMenu {
    public static final int RESULT_SLOT_X = 128;
    public static final int RESULT_SLOT_Y = 456;

    private final InteractionHand hand;
    private final ResourceLocation knappingTypeId;
    private final boolean useDisabledTexture;
    private final ResourceLocation activeCellsTexture;
    private final ResourceLocation disabledCellsTexture;
    private KnappingType type;
    private final KnappingState state;
    private final SimpleContainer resultContainer;
    private ItemStack result = ItemStack.EMPTY;
    private boolean consumed;

    private KnappingMenu(int containerId, Inventory inventory, InteractionHand hand, ResourceLocation knappingTypeId, int width, int height, boolean useDisabledTexture, ResourceLocation activeCellsTexture, ResourceLocation disabledCellsTexture) {
        super(ModMenus.KNAPPING.get(), containerId);
        this.hand = hand;
        this.knappingTypeId = knappingTypeId;
        this.type = KnappingTypeManager.INSTANCE.get(knappingTypeId).orElse(null);
        this.useDisabledTexture = useDisabledTexture;
        this.activeCellsTexture = activeCellsTexture;
        this.disabledCellsTexture = disabledCellsTexture;
        this.state = new KnappingState(width, height);
        this.resultContainer = new SimpleContainer(1);
        this.consumed = false;
        addResultSlot();
        addPlayerInventorySlots(inventory);
    }

    public static KnappingMenu client(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        ResourceLocation typeId = buffer.readResourceLocation();
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        int width = buffer.readVarInt();
        int height = buffer.readVarInt();
        boolean useDisabledTexture = buffer.readBoolean();
        ResourceLocation activeCellsTexture = buffer.readResourceLocation();
        ResourceLocation disabledCellsTexture = buffer.readResourceLocation();
        return new KnappingMenu(containerId, inventory, hand, typeId, width, height, useDisabledTexture, activeCellsTexture, disabledCellsTexture);
    }

    public static KnappingMenu server(int containerId, Inventory inventory, InteractionHand hand, KnappingType type) {
        KnappingMenu menu = new KnappingMenu(
                containerId,
                inventory,
                hand,
                type.id(),
                type.gridWidth(),
                type.gridHeight(),
                type.useDisabledTexture(),
                type.activeCellsTexture(),
                type.disabledCellsTexture()
        );
        menu.type = type;
        return menu;
    }

    public int getGridWidth() {
        return state.width();
    }

    public int getGridHeight() {
        return state.height();
    }

    public boolean isRemoved(int x, int y) {
        return state.isRemoved(x, y);
    }

    public ItemStack getResultStack() {
        return result;
    }

    public void clientApplySync(long removedMask, ItemStack result, boolean consumed) {
        this.state.setRemovedMask(removedMask);
        this.result = result;
        this.resultContainer.setItem(0, result.copy());
        this.consumed = consumed;
    }

    public void handleServerClick(ServerPlayer player, int x, int y) {
        if (type == null || x < 0 || y < 0 || x >= state.width() || y >= state.height()) {
            return;
        }

        if (state.isRemoved(x, y)) {
            return;
        }

        if (!type.consumeAfterComplete() && !consumed) {
            if (!tryConsumeInput(player, type.amountToConsume())) {
                player.closeContainer();
                return;
            }
            consumed = true;
        }

        if (!state.removeCell(x, y)) {
            return;
        }

        SoundEvent clickSound = type.resolveClickSound();
        if (clickSound != null) {
            player.level().playSound(null, player.blockPosition(), clickSound, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        updateResult(player);
        sync(player);
    }

    public void handleServerTakeResult(ServerPlayer player, ItemStack takenStack) {
        if (type == null || result.isEmpty()) {
            return;
        }

        if (!canTakeResult(player)) {
            return;
        }

        if (type.consumeAfterComplete() && !tryConsumeInput(player, type.amountToConsume())) {
            player.displayClientMessage(Component.translatable("message.agesprimitives.requires_more_material"), true);
            return;
        }

        result = ItemStack.EMPTY;
        resultContainer.setItem(0, ItemStack.EMPTY);
        SoundEvent takeSound = type.resolveResultTakeSound();
        if (takeSound != null) {
            player.level().playSound(null, player.blockPosition(), takeSound, SoundSource.PLAYERS, 0.9F, 1.0F);
        }

        state.clear();
        consumed = false;

        if (!hasEnoughInputForNext(player)) {
            player.closeContainer();
            return;
        }

        updateResult(player);
        sync(player);
    }

    public void onOpened(ServerPlayer player) {
        type = KnappingTypeManager.INSTANCE.get(knappingTypeId).orElse(type);
        updateResult(player);
        sync(player);
    }

    public void onDatapackReload(ServerPlayer player) {
        type = KnappingTypeManager.INSTANCE.get(knappingTypeId).orElse(null);
        if (type == null || type.gridWidth() != state.width() || type.gridHeight() != state.height()) {
            player.closeContainer();
            return;
        }
        updateResult(player);
        sync(player);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    private boolean hasEnoughInputForNext(Player player) {
        if (type == null) {
            return false;
        }
        ItemStack handStack = player.getItemInHand(hand);
        return type.input().matches(handStack) && handStack.getCount() >= type.input().count();
    }

    private boolean tryConsumeInput(Player player, int amount) {
        ItemStack handStack = player.getItemInHand(hand);
        if (type == null || !type.input().matches(handStack) || handStack.getCount() < amount) {
            return false;
        }
        handStack.shrink(amount);
        return true;
    }

    private void updateResult(ServerPlayer player) {
        Optional<KnappingRecipe> match = findMatch(player);
        result = match.map(recipe -> recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY)).orElse(ItemStack.EMPTY);
        resultContainer.setItem(0, result.copy());
    }

    private Optional<KnappingRecipe> findMatch(ServerPlayer player) {
        return KnappingRecipeIndex.find(player.serverLevel().getRecipeManager(), knappingTypeId, state.width(), state.height(), state.removedMask());
    }

    private void sync(ServerPlayer player) {
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2CKnappingSyncPacket(containerId, state.removedMask(), result, consumed));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index == 0) {
            Slot slot = slots.get(index);
            if (!slot.hasItem()) {
                return ItemStack.EMPTY;
            }
            ItemStack slotStack = slot.getItem();
            ItemStack copy = slotStack.copy();
            if (!moveItemStackTo(slotStack, 1, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
            return copy;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
    }

    public ResourceLocation getKnappingTypeId() {
        return knappingTypeId;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public boolean useDisabledTexture() {
        return useDisabledTexture;
    }

    public ResourceLocation getActiveCellsTexture() {
        return activeCellsTexture;
    }

    public ResourceLocation getDisabledCellsTexture() {
        return disabledCellsTexture;
    }

    private boolean canTakeResult(Player player) {
        if (type == null || result.isEmpty()) {
            return false;
        }
        if (!type.consumeAfterComplete()) {
            return true;
        }
        ItemStack handStack = player.getItemInHand(hand);
        return type.input().matches(handStack) && handStack.getCount() >= type.amountToConsume();
    }

    private void addResultSlot() {
        addSlot(new Slot(resultContainer, 0, RESULT_SLOT_X, RESULT_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return canTakeResult(player);
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                if (player instanceof ServerPlayer serverPlayer) {
                    handleServerTakeResult(serverPlayer, stack);
                }
            }
        });
    }

    private void addPlayerInventorySlots(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 162));
        }
    }

    public static class Provider implements MenuProvider {
        private final ResourceLocation typeId;
        private final InteractionHand hand;

        public Provider(ResourceLocation typeId, InteractionHand hand) {
            this.typeId = typeId;
            this.hand = hand;
        }

        @Override
        public Component getDisplayName() {
            return Component.translatable("screen.agesprimitives.knapping");
        }

        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            KnappingType type = KnappingTypeManager.INSTANCE.get(typeId).orElse(null);
            if (type == null) {
                return new KnappingMenu(
                        id,
                        inventory,
                        hand,
                        typeId,
                        5,
                        5,
                        false,
                        new ResourceLocation("agesprimitives", "textures/gui/knapping/cells/rock_active.png"),
                        new ResourceLocation("agesprimitives", "textures/gui/knapping/cells/rock_disabled.png")
                );
            }
            return KnappingMenu.server(id, inventory, hand, type);
        }
    }
}
