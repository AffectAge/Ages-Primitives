package com.agesprimitives.config;

import com.agesprimitives.AgesPrimitives;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ModCommonConfig {
    public static final ForgeConfigSpec SPEC;

    private static final Map<ResourceLocation, OpenRequirement> OPEN_REQUIREMENTS = new LinkedHashMap<>();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("knapping_openers");
        registerType(builder, "rock", "minecraft:cobblestone", 1);
        registerType(builder, "flint", "minecraft:flint", 1);
        registerType(builder, "clay", "minecraft:clay_ball", 1);
        registerType(builder, "fire_clay", "minecraft:brick", 1);
        registerType(builder, "leather", "minecraft:leather", 1);
        registerType(builder, "bone", "minecraft:bone", 1);
        builder.pop();
        SPEC = builder.build();
    }

    private static void registerType(ForgeConfigSpec.Builder builder, String typePath, String defaultItem, int defaultCount) {
        builder.push(typePath);
        ForgeConfigSpec.ConfigValue<String> item = builder
                .comment("Item used to open " + typePath + " knapping")
                .define("open_item", defaultItem);
        ForgeConfigSpec.IntValue count = builder
                .comment("Required item count in hand to open " + typePath + " knapping")
                .defineInRange("open_count", defaultCount, 1, 64);
        builder.pop();
        OPEN_REQUIREMENTS.put(new ResourceLocation(AgesPrimitives.MOD_ID, typePath), new OpenRequirement(item, count));
    }

    public static Optional<ResourceLocation> findTypeForStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        for (Map.Entry<ResourceLocation, OpenRequirement> entry : OPEN_REQUIREMENTS.entrySet()) {
            Item configured = entry.getValue().resolveItem();
            if (configured != Items.AIR && stack.is(configured)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public static int getRequiredCount(ResourceLocation typeId, int fallback) {
        OpenRequirement requirement = OPEN_REQUIREMENTS.get(typeId);
        if (requirement == null) {
            return fallback;
        }
        return requirement.count().get();
    }

    public static boolean matchesTypeInput(ResourceLocation typeId, ItemStack stack) {
        OpenRequirement requirement = OPEN_REQUIREMENTS.get(typeId);
        if (requirement == null) {
            return false;
        }
        Item configured = requirement.resolveItem();
        return configured != Items.AIR && stack.is(configured);
    }

    private record OpenRequirement(ForgeConfigSpec.ConfigValue<String> item, ForgeConfigSpec.IntValue count) {
        Item resolveItem() {
            ResourceLocation id = ResourceLocation.tryParse(item.get());
            if (id == null) {
                return Items.AIR;
            }
            Item resolved = ForgeRegistries.ITEMS.getValue(id);
            return resolved != null ? resolved : Items.AIR;
        }
    }

    private ModCommonConfig() {
    }
}
