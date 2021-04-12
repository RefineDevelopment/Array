package me.drizzy.practice.util.tab.utils.serverversion;

import me.drizzy.practice.util.tab.utils.serverversion.impl.*;
import org.bukkit.*;


public class ServerVersionHandler {

    public static IServerVersion version;
    public static String serverVersionName;

    public ServerVersionHandler() {
        serverVersionName = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        version = new ServerVersionUnknownImpl();
    }
}
