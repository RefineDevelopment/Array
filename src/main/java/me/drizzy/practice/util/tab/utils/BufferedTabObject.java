package me.drizzy.practice.util.tab.utils;

import me.drizzy.practice.util.tab.*;
import lombok.*;

@Getter @Setter
public class BufferedTabObject
{
    public static final BufferedTabObject EMPTY_COLUMN = new BufferedTabObject().ping(0).text("").skin(ZigguratCommons.defaultTexture);;
    private TabColumn column;
    private int ping;
    private int slot;
    private String text;
    private SkinTexture skinTexture;

    public BufferedTabObject() {
        this.column = TabColumn.LEFT;
        this.ping = 0;
        this.slot = 1;
        this.text = "";
        this.skinTexture = ZigguratCommons.defaultTexture;
    }

    public BufferedTabObject text(final String text) {
        this.text = text;
        return this;
    }

    public BufferedTabObject skin(final SkinTexture skinTexture) {
        this.skinTexture = skinTexture;
        return this;
    }

    public BufferedTabObject slot(final Integer slot) {
        this.slot = slot;
        if (this.slot > 20 && this.slot <= 40) {
            this.slot -= 20;
            this.column = TabColumn.MIDDLE;
        }
        else if (this.slot > 40 && this.slot <= 60) {
            this.slot -= 40;
            this.column = TabColumn.RIGHT;
        }
        else if (this.slot > 60 && this.slot <= 80) {
            this.slot -= 60;
            this.column = TabColumn.FAR_RIGHT;
        }
        return this;
    }

    public BufferedTabObject ping(final Integer ping) {
        this.ping = ping;
        return this;
    }

    public BufferedTabObject column(final TabColumn tabColumn) {
        this.column = tabColumn;
        if (this.slot > 20 && this.slot <= 40) {
            this.slot -= 20;
            this.column = TabColumn.MIDDLE;
        }
        else if (this.slot > 40 && this.slot <= 60) {
            this.slot -= 40;
            this.column = TabColumn.RIGHT;
        }
        else if (this.slot > 60 && this.slot <= 80) {
            this.slot -= 60;
            this.column = TabColumn.FAR_RIGHT;
        }
        return this;
    }
}
