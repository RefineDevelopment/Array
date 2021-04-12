package me.drizzy.practice.util.tab.utils;

import org.bukkit.*;

import java.util.*;

public class LegacyClientUtils {

    public static ArrayList<String> tabEntrys = getTabEntrys();
    public static ArrayList<String> teamNames = getTeamNames();

    private static ArrayList<String> getTabEntrys() {
        ArrayList<String> list = new ArrayList<>();
        for(int i=1; i<=15; i++) {
            String entry = ChatColor.values()[i].toString();
            list.add(ChatColor.RED + entry);
            list.add(ChatColor.GREEN + entry);
            list.add(ChatColor.DARK_RED + entry);
            list.add(ChatColor.DARK_GREEN + entry);
            list.add(ChatColor.BLUE + entry);
            list.add(ChatColor.DARK_BLUE + entry);
        }
        return list;
    }

    private static ArrayList<String> getTeamNames() {

        final ArrayList<String> list = new ArrayList<>();

        for(int i=0; i<80; i++) {
            String s = (i<10 ? "\\u00010" : "\\u0001") + i;
            list.add(s);
        }

        return list;
    }

}
