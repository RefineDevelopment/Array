package me.drizzy.practice.util.tab.utils.playerversion.impl;

import me.drizzy.practice.util.tab.utils.playerversion.*;
import org.bukkit.entity.*;
import us.myles.ViaVersion.api.Via;

public class PlayerVersion1_7Impl implements IPlayerVersion {

    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(
                Via.getAPI().getPlayerVersion(player)
        );
    }
}
