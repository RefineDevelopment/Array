package me.drizzy.practice.util.tab.utils;

import me.drizzy.practice.util.tab.*;
import lombok.*;
import org.bukkit.*;

@Getter @Setter @AllArgsConstructor
public class TabEntry {

    private String id;
    private OfflinePlayer offlinePlayer;
    private String text;
    private ZigguratTablist tab;
    private SkinTexture texture;
    private TabColumn column;
    private int slot, rawSlot, latency;

}
