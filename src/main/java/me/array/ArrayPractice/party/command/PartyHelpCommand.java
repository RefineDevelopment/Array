package me.array.ArrayPractice.party.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "party", "party help", "p", "p help" })
public class PartyHelpCommand
{
    private final static String[] HELP_MESSAGE = new String[] {
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
            ChatColor.AQUA + "Party" + ChatColor.GRAY + " » All Commands For Parties." ,
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
            ChatColor.AQUA + "» /party help " + ChatColor.GRAY + "- Displays the help message",
            ChatColor.AQUA + "» /party create " + ChatColor.GRAY + "- Creates a party instance",
            ChatColor.AQUA + "» /party leave " + ChatColor.GRAY + "- Leave your current party",
            ChatColor.AQUA + "» /party info " + ChatColor.GRAY + "- Displays your party information",
            ChatColor.AQUA + "» /party join (party) " + ChatColor.GRAY + "- Join a party (invited or unlocked)",
            "",
            ChatColor.AQUA + "» /party open " + ChatColor.GRAY + "- Open your party for others to join",
            ChatColor.AQUA + "» /party close " + ChatColor.GRAY + "- Close your party for others to join",
            ChatColor.AQUA + "» /party invite (player) " + ChatColor.GRAY + "- Invites a profile to your party",
            ChatColor.AQUA + "» /party kick (player) " + ChatColor.GRAY + "- Kicks a profile from your party",
            ChatColor.AQUA + "» /party ban (player) " + ChatColor.GRAY + "- Bans a profile from your party",
            ChatColor.AQUA + "» /party unban (player) " + ChatColor.GRAY + "- Unbans a profile from your party",
            ChatColor.AQUA + "» /party leader (player) " + ChatColor.GRAY + "- Transfers Ownership of your party",
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
    };
    public void execute(final Player player) {
            player.sendMessage(HELP_MESSAGE);
        }
    }