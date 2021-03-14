package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.chat.CC;

@CommandMeta(label={"array ver", "array version"})
public class ArrayVerCommand {
    public void execute(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        p.sendMessage(CC.translate("&7This server is running &bArray &8[&7Commercial Build&8]"));
        p.sendMessage(CC.translate("&7Array is made By &bDrizzy&7, &b&lNick &7(Base by Him) and &bZentil."));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
    }
}
