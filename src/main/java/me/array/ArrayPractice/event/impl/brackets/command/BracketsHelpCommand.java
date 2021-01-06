package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets", "brackets help"}, permission = "practice.bracketshelp")
public class BracketsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lBRACKETS &7- &7Information on how to use bracket commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7» &b/brackets cancel &7- &7Cancel current Brackets Event"));
        player.sendMessage(Color.translate("&7» &b/brackets cooldown &7- &7Reset the Brackets Event cooldown"));
        player.sendMessage(Color.translate("&7» &b/brackets host &7- &7Host a Brackets Event"));
        player.sendMessage(Color.translate("&7» &b/brackets join &7- &7Join ongoing Brackets Event"));
        player.sendMessage(Color.translate("&7» &b/brackets leave &7- &7Leave ongoing Brackets Event"));
        player.sendMessage(Color.translate("&7» &b/brackets tp &7- &7Teleport to the Brackets Event Arena"));
        player.sendMessage(Color.translate("&7» &b/brackets setspawn  &7- &7Set the spawns for Brackets Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
