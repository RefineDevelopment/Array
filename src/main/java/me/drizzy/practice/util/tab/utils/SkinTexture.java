package me.drizzy.practice.util.tab.utils;

import lombok.*;

@Setter
public class SkinTexture {

    public String SKIN_VALUE;
    public String SKIN_SIGNATURE;

    public SkinTexture(String skinValue, String skinSig) {
        this.SKIN_VALUE = skinValue;
        this.SKIN_SIGNATURE = skinSig;
    }

    @Override
    public String toString() {
        return SKIN_SIGNATURE + SKIN_VALUE;
    }
}
