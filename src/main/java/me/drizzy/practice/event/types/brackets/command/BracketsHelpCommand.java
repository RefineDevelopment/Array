package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets", "brackets help"}, permission = "array.dev")
public class BracketsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&b&lBrackets &8- &8&o(&7&o&7Bracket Commands&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &b/brackets cancel &8- &8&o(&7&o&7Cancel current Brackets Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/brackets cooldown &8- &8&o(&7&o&7Reset the Brackets Event cooldown&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/brackets host &8- &8&o(&7&o&7Host a Brackets Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/brackets forcestart &8- &8&o(&7&o&7Force start a Brackets Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/brackets join &8- &8&o(&7&o&7Join ongoing Brackets Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/brackets leave &8- &8&o(&7&o&7Leave ongoing Brackets Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/brackets tp &8- &8&o(&7&o&7Teleport to the Brackets Event Arena&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/brackets setspawn  &8- &8&o(&7&o&7Set the spawns for Brackets Event&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
