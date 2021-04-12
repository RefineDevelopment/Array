package me.drizzy.practice.util.tab.utils.playerversion;

import me.drizzy.practice.util.tab.utils.playerversion.impl.*;

public class PlayerVersionHandler {

    public static IPlayerVersion version;

    public PlayerVersionHandler() {
        version = new PlayerVersion1_7Impl();
    }
}
