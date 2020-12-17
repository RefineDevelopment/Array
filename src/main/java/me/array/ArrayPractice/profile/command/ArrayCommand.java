package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "array", "practice", "core" })
public class ArrayCommand {
    public void execute(final Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(CC.translate("&7This server is running the &b&lArray Practice&7 on version 2.0"));
        player.sendMessage(CC.translate("&7Forked By Drizzy for MoonNight Network!"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

