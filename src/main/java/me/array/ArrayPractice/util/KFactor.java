package me.array.ArrayPractice.util;

import lombok.Getter;

@Getter
public class KFactor {

    @Getter
    private final int startIndex;
    @Getter
    private final int endIndex;
    @Getter
    private final double value;

    public KFactor(final int startIndex, final int endIndex, final double value) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.value = value;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public double getValue() {
        return this.value;
    }
}
