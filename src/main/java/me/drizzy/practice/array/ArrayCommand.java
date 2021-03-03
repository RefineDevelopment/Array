package me.drizzy.practice.array;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"array", "array help", "practice"})
public class ArrayCommand {
    public void execute(Player p) {
        if (p.hasPermission("array.staff")) {
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            p.sendMessage(CC.translate("&bArray &7» Array Commands"));
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            p.sendMessage(CC.translate("&b/array reload &8- &8&o(&7&oReload the plugin (NOT RECOMMENDED)."));
            p.sendMessage(CC.translate("&b/array setlobby &8- &8&o(&7&oSets the lobby to player's location."));
            p.sendMessage(CC.translate("&b/array savekits &8- &8&o(&7&oSave all Kits."));
            p.sendMessage(CC.translate("&b/array savearenas &8- &8&o(&7&oSave all Arenas"));
            p.sendMessage(CC.translate("&b/array savedata &8- &8&o(&7&oSave all Profiles."));
            p.sendMessage(CC.translate("&b/array hcf &8- &8&o(&7&oHelp on how to setup HCF."));
            p.sendMessage(CC.translate("&b/array resetstats (name) &8- &8&o(&7&oResets a profile."));
            p.sendMessage(CC.translate("&b/array rename (name) &8- &8&o(&7&oRenames item in hand."));
            p.sendMessage(CC.translate("&b/kit help &8- &8&o(&7&oView kit commands."));
            p.sendMessage(CC.translate("&b/arena help &8- &8&o(&7&oView arena commands."));
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        } else {
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            p.sendMessage(CC.translate("&7This server is running &bArray [Commercial Build]"));
            p.sendMessage(CC.translate("&7Array is made By Drizzy, Nick and Zentil"));
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");

        }
    }
}
