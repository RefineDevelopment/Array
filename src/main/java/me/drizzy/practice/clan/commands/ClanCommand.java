package me.drizzy.practice.clan.commands;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

/**
 * @author Drizzy
 * Created at 5/13/2021
 */

@CommandMeta(label = "clan")
public class ClanCommand {
    
    public void execute(CommandSender player) {
        player.sendMessage(new String[]{
                CC.CHAT_BAR,
                CC.RED + CC.BOLD + "Clans Help " + CC.GRAY + "-" + CC.RESET + " Information on how to use clan commands",
                CC.CHAT_BAR,
                CC.RED + CC.BOLD + "General Commands:",
                CC.RED + "/clan create " + CC.GRAY + "- Create a new clan",
                CC.RED + "/clan leave " + CC.GRAY + "- Leave your current clan",
                CC.RED + "/clan accept [clan|player] " + CC.GRAY + "- Accept clan invitation",
                CC.RED + "/clan info [clan|player] " + CC.GRAY + "- View a clan's information",
                "",
                CC.RED + CC.BOLD + "Leader Commands:",
                CC.RED + "/clan disband " + CC.GRAY + "- Disband your clan",
                CC.RED + "/clan ban <player> " + CC.GRAY + "- Ban a player from your clan",
                CC.RED + "/clan unban <player> " + CC.GRAY + "- Unban a player from your clan",
                CC.RED + "/clan description <text> " + CC.GRAY + "- Set your clan's description",
                CC.RED + "/clan password <password> " + CC.GRAY + "- Sets clan password",
                CC.RED + "/clan promote <player> " + CC.GRAY + "- Promote a player",
                CC.RED + "/clan demote <player> " + CC.GRAY + "- Demote a player",
                "",
                CC.RED + CC.BOLD + "Captain Commands:",
                CC.RED + "/clan invite <player> " + CC.GRAY + "- Invite a player to join your clan",
                CC.RED + "/clan kick <player> " + CC.GRAY + "- Kick a player from your clan",
                "",
                CC.RED + CC.BOLD + "Other Help:",
                CC.RED + "To use " + CC.PINK + "clan chat" + CC.RED + ", prefix your messages with the " + CC.GRAY + "'" + CC.PINK + "." + CC.GRAY + "'" + CC.RED + " sign.",
                CC.RED + "Clans are limited to " + CC.PINK + "20 members" + CC.RED + ".",
                CC.CHAT_BAR
        });
    }
}
