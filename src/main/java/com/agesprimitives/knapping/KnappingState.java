package com.agesprimitives.knapping;

import java.util.Objects;

public final class KnappingState {
    private final int width;
    private final int height;
    private long removedMask;

    public KnappingState(int width, int height) {
        this.width = width;
        this.height = height;
        this.removedMask = 0L;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public long removedMask() {
        return removedMask;
    }

    public boolean isRemoved(int x, int y) {
        return (removedMask & bit(x, y)) != 0;
    }

    public boolean removeCell(int x, int y) {
        long bit = bit(x, y);
        if ((removedMask & bit) != 0L) {
            return false;
        }
        removedMask |= bit;
        return true;
    }

    public void setRemovedMask(long removedMask) {
        this.removedMask = removedMask;
    }

    public void clear() {
        removedMask = 0L;
    }

    private long bit(int x, int y) {
        Objects.checkIndex(x, width);
        Objects.checkIndex(y, height);
        return 1L << (y * width + x);
    }
}
