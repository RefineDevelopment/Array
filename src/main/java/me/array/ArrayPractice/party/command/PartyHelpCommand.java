package me.array.ArrayPractice.party.command;

import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "party", "party help", "p", "p help" })
public class PartyHelpCommand
{
    private final static String[] HELP_MESSAGE = new String[] {
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
            CC.translate("&bParty" + ChatColor.GRAY + " » All Commands For Parties."),
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
            CC.translate("&7»&b /party help " + ChatColor.GRAY + "- Displays the help message"),
            CC.translate("&7»&b /party create " + ChatColor.GRAY + "- Creates a party instance"),
            CC.translate("&7»&b /party leave " + ChatColor.GRAY + "- Leave your current party"),
            CC.translate("&7»&b /party info " + ChatColor.GRAY + "- Displays your party information"),
            CC.translate("&7»&b /party join (party) " + ChatColor.GRAY + "- Join a party (invited or unlocked)"),
            "",
            CC.translate("&7»&b /party open " + ChatColor.GRAY + "- Open your party for others to join"),
            CC.translate("&7»&b /party close " + ChatColor.GRAY + "- Close your party for others to join"),
            CC.translate("&7»&b /party invite (player) " + ChatColor.GRAY + "- Invites a profile to your party"),
            CC.translate("&7»&b /party kick (player) " + ChatColor.GRAY + "- Kicks a profile from your party"),
            CC.translate("&7»&b /party ban (player) " + ChatColor.GRAY + "- Bans a profile from your party"),
            CC.translate("&7»&b /party unban (player) " + ChatColor.GRAY + "- Unbans a profile from your party"),
            CC.translate("&7»&b /party leader (player) " + ChatColor.GRAY + "- Transfers Ownership of your party"),
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
    };
    public void execute(final Player player) {
            player.sendMessage(HELP_MESSAGE);
        }
    }