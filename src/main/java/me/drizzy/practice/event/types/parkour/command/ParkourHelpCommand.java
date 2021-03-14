package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.chat.Color;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour", "parkour help"}, permission = "array.dev")
public class ParkourHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&b&lPARKOUR &8- &8&o(&7&o&7Parkour Commands&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &b/parkour cancel &8- &8&o(&7&o&7Cancel current Parkour Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/parkour cooldown &8- &8&o(&7&o&7Reset the Parkour Event cooldown&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/parkour host &8- &8&o(&7&o&7Host a Parkour Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/parkour forcestart &8- &8&o(&7&o&7Force Start a Parkour Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/parkour join &8- &8&o(&7&o&7Join ongoing Parkour Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/parkour leave &8- &8&o(&7&o&7Leave ongoing Parkour Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/parkour tp &8- &8&o(&7&o&7Teleport to the Parkour Event Arena&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/parkour setspawn  &8- &8&o(&7&o&7Set the spawns for Parkour Event&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
