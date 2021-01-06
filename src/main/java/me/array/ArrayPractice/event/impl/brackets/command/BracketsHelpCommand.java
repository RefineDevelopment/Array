package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets", "brackets help"}, permission = "practice.bracketshelp")
public class BracketsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&4&lBRACKETS &8- &7Information on how to use bracket commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7/brackets cancel &8- &7Cancel current Brackets Event"));
        player.sendMessage(Color.translate("&7/brackets cooldown &8- &7Reset the Brackets Event cooldown"));
        player.sendMessage(Color.translate("&7/brackets host &8- &7Host a Brackets Event"));
        player.sendMessage(Color.translate("&7/brackets join &8- &7Join ongoing Brackets Event"));
        player.sendMessage(Color.translate("&7/brackets leave &8- &7Leave ongoing Brackets Event"));
        player.sendMessage(Color.translate("&7/brackets tp &8- &7Teleport to the Brackets Event Arena"));
        player.sendMessage(Color.translate("&7/brackets setspawn  &8- &7Set the spawns for Brackets Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
