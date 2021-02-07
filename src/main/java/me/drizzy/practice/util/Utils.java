/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.ComponentBuilder
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package me.drizzy.practice.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.drizzy.practice.Array;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utils {
    private static HashMap<Player, InventoryRestore> inventoryRestore = new HashMap();
    private static final int CENTER_PX = 154;

    public static Array getInstance() {
        return Array.getInstance();
    }

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)string);
    }

    public static void setValue(String string, Class<?> class_, Object object, Object object2) {
        try {
            Field field = class_.getDeclaredField(string);
            field.setAccessible(true);
            field.set(object, object2);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public static void sendPerformCommand(Player player, String string, String string2, String string3) {
        TextComponent textComponent = new TextComponent(Utils.translate(string2));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, string));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.translate(string3)).create()));
        player.spigot().sendMessage((BaseComponent)textComponent);
    }

    public static Location convertingLocation(String string) {
        String[] arrstring = string.split(",");
        World world = Bukkit.getWorld((String)arrstring[0]);
        double d2 = Double.parseDouble(arrstring[1]);
        double d3 = Double.parseDouble(arrstring[2]);
        double d4 = Double.parseDouble(arrstring[3]);
        float f2 = Float.parseFloat(arrstring[4]);
        float f3 = Float.parseFloat(arrstring[5]);
        return new Location(world, d2, d3, d4, f2, f3);
    }

    public static String convertingString(Location location) {
        return String.valueOf(location.getWorld().getName()) + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static List<String> setConvertingLocations(List<Location> list) {
        ArrayList<String> arrayList = new ArrayList<String>();
        list.forEach(location -> {
            boolean bl2 = arrayList.add(Utils.convertingString(location));
        });
        return arrayList;
    }

    public static List<Location> getConvertingStrings(List<String> list) {
        ArrayList<Location> arrayList = new ArrayList<Location>();
        list.forEach(string -> {
            boolean bl2 = arrayList.add(Utils.convertingLocation(string));
        });
        return arrayList;
    }

    public static void saveInventoryPlayer(Player player) {
        inventoryRestore.put(player, new InventoryRestore(player));
    }

    public static void givePlayerInventory(Player player) {
        if (inventoryRestore.containsKey((Object)player)) {
            inventoryRestore.get((Object)player).restore();
            player.updateInventory();
            inventoryRestore.remove((Object)player);
            player.updateInventory();
        }
    }
    public static void resetPlayer(Player player, boolean bl2, boolean bl3) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setWalkSpeed(0.2f);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        if (bl2) {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.updateInventory();
        }
        if (bl3) {
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
    }

    public static String formatIntoHHMMSS(int n2) {
        int n3 = n2 % 3600;
        int n4 = n3 / 60;
        int n5 = n3 % 60;
        return (n4 < 10 ? "0" : "") + n4 + ":" + (n5 < 10 ? "0" : "") + n5;
    }

    public static void sendCenteredMessage(Player player, String string) {
        Object object;
        int c22;
        if (string == null || string.equals("")) {
            player.sendMessage("");
        }
        string = ChatColor.translateAlternateColorCodes((char)'&', (String)string);
        int n2 = 0;
        boolean bl2 = false;
        boolean bl3 = false;
        for (char c223 : string.toCharArray()) {
            if (c223 == '\u00a7') {
                bl2 = true;
                continue;
            }
            if (bl2) {
                bl2 = false;
                if (c223 == 'l' || c223 == 'L') {
                    bl3 = true;
                    continue;
                }
                bl3 = false;
                continue;
            }
            object = DefaultFontInfo.getDefaultFontInfo(c223);
            n2 += bl3 ? ((DefaultFontInfo)((Object)object)).getBoldLength() : ((DefaultFontInfo)((Object)object)).getLength();
            ++n2;
        }
        c22 = n2 / 2;
        int n3 = 154 - c22;
        int n4 = DefaultFontInfo.SPACE.getLength() + 1;
        object = new StringBuilder();
        for (int i2 = 0; i2 < n3; i2 += n4) {
            ((StringBuilder)object).append(" ");
        }
        player.sendMessage(String.valueOf(((StringBuilder)object).toString()) + string);
    }
}

