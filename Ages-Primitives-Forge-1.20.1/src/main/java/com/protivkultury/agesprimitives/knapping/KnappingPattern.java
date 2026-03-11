package com.protivkultury.agesprimitives.knapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class KnappingPattern
{
    public static final int MAX_WIDTH = 5;
    public static final int MAX_HEIGHT = 5;

    public static KnappingPattern fromJson(JsonObject json)
    {
        final JsonArray array = GsonHelper.getAsJsonArray(json, "pattern");
        final boolean outsideSlotRequired = GsonHelper.getAsBoolean(json, "outside_slot_required", true);

        final int height = array.size();
        if (height <= 0 || height > MAX_HEIGHT)
        {
            throw new JsonSyntaxException("Pattern height must be in range [1, " + MAX_HEIGHT + "]");
        }

        final int width = GsonHelper.convertToString(array.get(0), "pattern[0]").length();
        if (width <= 0 || width > MAX_WIDTH)
        {
            throw new JsonSyntaxException("Pattern width must be in range [1, " + MAX_WIDTH + "]");
        }

        final KnappingPattern pattern = new KnappingPattern(width, height, outsideSlotRequired);
        for (int y = 0; y < height; y++)
        {
            final String row = GsonHelper.convertToString(array.get(y), "pattern[" + y + "]");
            if (row.length() != width)
            {
                throw new JsonSyntaxException("All rows must have the same width");
            }
            for (int x = 0; x < width; x++)
            {
                pattern.set(x + y * width, row.charAt(x) != ' ');
            }
        }
        return pattern;
    }

    public static KnappingPattern fromNetwork(FriendlyByteBuf buffer)
    {
        final int width = buffer.readVarInt();
        final int height = buffer.readVarInt();
        final int data = buffer.readInt();
        final boolean outsideSlotRequired = buffer.readBoolean();
        return new KnappingPattern(width, height, data, outsideSlotRequired);
    }

    private final int width;
    private final int height;
    private final boolean outsideSlotRequired;
    private int data;

    public KnappingPattern()
    {
        this(MAX_WIDTH, MAX_HEIGHT, false);
    }

    public KnappingPattern(int width, int height, boolean outsideSlotRequired)
    {
        this(width, height, (1 << (width * height)) - 1, outsideSlotRequired);
    }

    private KnappingPattern(int width, int height, int data, boolean outsideSlotRequired)
    {
        this.width = width;
        this.height = height;
        this.data = data;
        this.outsideSlotRequired = outsideSlotRequired;
    }

    public int data()
    {
        return data;
    }

    public void setData(int data)
    {
        this.data = data;
    }

    public void setAll(boolean value)
    {
        data = value ? (1 << (width * height)) - 1 : 0;
    }

    public boolean get(int index)
    {
        return ((data >> index) & 0b1) == 1;
    }

    public void set(int index, boolean value)
    {
        if (value)
        {
            data |= 1 << index;
        }
        else
        {
            data &= ~(1 << index);
        }
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(width);
        buffer.writeVarInt(height);
        buffer.writeInt(data);
        buffer.writeBoolean(outsideSlotRequired);
    }

    public boolean matches(KnappingPattern other)
    {
        for (int dx = 0; dx <= this.width - other.width; dx++)
        {
            for (int dy = 0; dy <= this.height - other.height; dy++)
            {
                if (matches(other, dx, dy, false) || matches(other, dx, dy, true))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(KnappingPattern other, int startX, int startY, boolean mirrored)
    {
        for (int x = 0; x < this.width; x++)
        {
            for (int y = 0; y < this.height; y++)
            {
                final int patternIndex = y * width + x;
                if (x < startX || y < startY || x - startX >= other.width || y - startY >= other.height)
                {
                    if (get(patternIndex) != other.outsideSlotRequired)
                    {
                        return false;
                    }
                }
                else
                {
                    final int otherIndex;
                    if (mirrored)
                    {
                        otherIndex = (y - startY) * other.width + (other.width - 1 - (x - startX));
                    }
                    else
                    {
                        otherIndex = (y - startY) * other.width + (x - startX);
                    }
                    if (get(patternIndex) != other.get(otherIndex))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
