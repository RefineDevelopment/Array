package me.drizzy.practice.array;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"array", "array help", "practice"})
public class ArrayCommand {
    public void execute(Player p) {
        if (p.hasPermission("practice.staff")) {
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            p.sendMessage(CC.translate("&bArray &7» Array Commands"));
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            p.sendMessage(CC.translate("&7» &b/array reload &7- Reload the plugin (NOT RECOMMENDED)."));
            p.sendMessage(CC.translate("&7» &b/array setlobby &7- Sets the lobby to player's location."));
            p.sendMessage(CC.translate("&7» &b/array savekits &7- Save all Kits."));
            p.sendMessage(CC.translate("&7» &b/array savearenas &7- Save all Arenas"));
            p.sendMessage(CC.translate("&7» &b/array savedata &7- Save all Profiles."));
            p.sendMessage(CC.translate("&7» &b/array hcf &7- Help on how to setup HCF."));
            p.sendMessage(CC.translate("&7» &b/array resetstats (name) &7- Resets a profile."));
            p.sendMessage(CC.translate("&7» &b/array rename (name) &7- Renames item in hand."));
            p.sendMessage(CC.translate("&7» &b/kit help &7- View kit commands."));
            p.sendMessage(CC.translate("&7» &b/arena help &7- View arena commands."));
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        } else {
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            p.sendMessage(CC.translate("&7This server is running &bArray [Commercial Build]"));
            p.sendMessage(CC.translate("&7Array is made By Drizzy, Nick and Zentil"));
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");

        }
    }
}
