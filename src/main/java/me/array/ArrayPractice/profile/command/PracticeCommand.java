package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "help", "?", "info" })
public class PracticeCommand {
        public void execute(final Player player) {
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
            player.sendMessage(CC.translate("         &b&lResolve Network " + ChatColor.GRAY + "(Practice)"));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() +"------------------------------------------------");
            player.sendMessage(CC.translate("&bPractice Commands:"));
            player.sendMessage(CC.translate("&7/duel <Name> &8- &7Send a player a duel request"));
            player.sendMessage(CC.translate("&7/spec <Name> &8- &7Spectate a player's ongoing match"));
            player.sendMessage(CC.translate("&7/settings &8- &7Edit preferences on the server"));
            player.sendMessage(CC.translate("&7/party &8- &7See all party related commands"));
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&7To send a message to your party do &7'&d@&7' <message>:"));
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&bServer Links:"));
            player.sendMessage(CC.translate("&7Website &8- &bwww.Resolve.rip"));
            player.sendMessage(CC.translate("&7Discord &8- &bdiscord.Resolve.rip"));
            player.sendMessage(CC.translate("&7Store &8- &bstore.Resolve.rip"));
            player.sendMessage("");
            player.sendMessage(CC.translate("&7Welcome to the &b&lResolve Practice Beta!"));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        }
    }
