package me.drizzy.practice.array;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"array", "array help", "practice"})
public class ArrayCommand {
    public void execute(Player p) {
        if (p.hasPermission("array.staff")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
            p.sendMessage(CC.translate("&bArray &7» Array Commands"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
            p.sendMessage(CC.translate(" &8• &b/array setlobby &8- &8&o(&7&oSets the lobby to player's location&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array savekits &8- &8&o(&7&oSave all Kits&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array savearenas &8- &8&o(&7&oSave all Arenas&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array savedata &8- &8&o(&7&oSave all Profiles&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array goldenhead &8- &8&o(&7&oReceive a pre-made G-Head&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array update &8- &8&o(&7&oUpdate all leaderboards&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array savedata &8- &8&o(&7&oSave all Profiles&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array savearenas &8- &8&o(&7&oSave all Arenas&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array savekits &8- &8&o(&7&oSave all Kits&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array hcf &8- &8&o(&7&oHelp on how to setup HCF&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array resetstats &8<&7name&8> &8- &8&o(&7&oResets a profile&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array rename &8<&7name&8> &8- &8&o(&7&oRenames item in hand&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/array spawn &8- &8&o(&7&oRefresh Profile & Teleport to spawn&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/kit help &8- &8&o(&7&oView kit commands&8&o)"));
            p.sendMessage(CC.translate(" &8• &b/arena help &8- &8&o(&7&oView arena commands&8&o)"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
            p.sendMessage(CC.translate("&7This server is running &bArray &8[&7Commercial Build&8]"));
            p.sendMessage(CC.translate("&7Array is made By &bDrizzy&7, &bNick &7and &bZentil"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));

        }
    }
}
