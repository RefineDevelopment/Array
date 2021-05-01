package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.chat.CC;

@CommandMeta(label={"array ver", "array version"})
public class ArrayVerCommand {
    public void execute(Player p) {
        p.sendMessage(CC.CHAT_BAR);
        p.sendMessage(CC.translate("&7This server is running &cArray &8[&71.0&8]"));
        p.sendMessage(CC.translate("&7Array is made By &c&lDrizzy &7and &cVeltus"));
        p.sendMessage(CC.translate("&7Base for &cArray &7provided by &cNick & Joeleoli"));
        p.sendMessage(CC.CHAT_BAR);
    }
}
