package com.agesprimitives.knapping;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KnappingTypeManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    public static final KnappingTypeManager INSTANCE = new KnappingTypeManager();

    private Map<ResourceLocation, KnappingType> typesById = Map.of();
    private List<KnappingType> ordered = List.of();

    private KnappingTypeManager() {
        super(GSON, "knapping_types");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, KnappingType> byId = ImmutableMap.builder();
        List<KnappingType> orderedList = new ArrayList<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : entries.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "knapping_type");
                KnappingType type = parse(fileId, json);
                byId.put(type.id(), type);
                orderedList.add(type);
            } catch (Exception ex) {
                LOGGER.error("Failed loading knapping type {}", fileId, ex);
            }
        }

        orderedList.sort(Comparator.comparing(type -> type.id().toString()));
        typesById = byId.buildOrThrow();
        ordered = List.copyOf(orderedList);
        LOGGER.info("Loaded {} knapping type definitions", ordered.size());
    }

    public Optional<KnappingType> get(ResourceLocation id) {
        return Optional.ofNullable(typesById.get(id));
    }

    public List<KnappingType> all() {
        return ordered;
    }

    public Optional<KnappingType> findForStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        for (KnappingType type : ordered) {
            if (type.input().matchesExactly(stack)) {
                return Optional.of(type);
            }
        }

        for (KnappingType type : ordered) {
            if (type.matches(stack)) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }

    private static KnappingType parse(ResourceLocation fileId, JsonObject json) {
        ResourceLocation id = new ResourceLocation(fileId.getNamespace(), fileId.getPath());

        KnappingType.InputRequirement input = KnappingType.InputRequirement.fromJson(GsonHelper.getAsJsonObject(json, "input"));
        int amountToConsume = GsonHelper.getAsInt(json, "amount_to_consume", 1);
        ResourceLocation clickSound = new ResourceLocation(GsonHelper.getAsString(json, "click_sound", "minecraft:block.stone.break"));
        ResourceLocation resultTakeSound = new ResourceLocation(GsonHelper.getAsString(json, "result_take_sound", "minecraft:entity.item.pickup"));
        boolean consumeAfterComplete = GsonHelper.getAsBoolean(json, "consume_after_complete", false);
        boolean useDisabledTexture = GsonHelper.getAsBoolean(json, "use_disabled_texture", false);
        boolean spawnsParticles = GsonHelper.getAsBoolean(json, "spawns_particles", true);
        int gridWidth = GsonHelper.getAsInt(json, "grid_width", 5);
        int gridHeight = GsonHelper.getAsInt(json, "grid_height", 5);
        ResourceLocation activeCellsTexture = new ResourceLocation(GsonHelper.getAsString(json, "active_cells_texture", "agesprimitives:textures/gui/knapping/cells/rock_active.png"));
        ResourceLocation disabledCellsTexture = new ResourceLocation(GsonHelper.getAsString(json, "disabled_cells_texture", "agesprimitives:textures/gui/knapping/cells/rock_disabled.png"));

        ItemStack jeiIcon = new ItemStack(Items.FLINT);
        if (json.has("jei_icon_item")) {
            JsonObject iconObj = GsonHelper.getAsJsonObject(json, "jei_icon_item");
            ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(iconObj, "item"));
            var item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item != null) {
                jeiIcon = new ItemStack(item);
            } else {
                LOGGER.warn("Unknown jei_icon_item {} in knapping type {}", itemId, fileId);
            }
        }

        if (gridWidth * gridHeight > 64) {
            throw new IllegalArgumentException("MVP supports max 64 knapping cells: " + id);
        }

        return new KnappingType(
                id,
                input,
                Math.max(1, amountToConsume),
                clickSound,
                resultTakeSound,
                consumeAfterComplete,
                useDisabledTexture,
                spawnsParticles,
                Math.max(1, gridWidth),
                Math.max(1, gridHeight),
                jeiIcon,
                activeCellsTexture,
                disabledCellsTexture
        );
    }
}
