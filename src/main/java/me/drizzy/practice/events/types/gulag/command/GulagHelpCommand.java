package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"gulag", "gulag help"}, permission = "array.dev")
public class GulagHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&b&lGulag &8- &8&o(&7&o&7Gulag Commands&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &b/gulag cancel &8- &8&o(&7&o&7Cancel current Gulag Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/gulag cooldown &8- &8&o(&7&o&7Reset the Gulag Event cooldown&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/gulag host &8- &8&o(&7&o&7Host a Gulag Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/gulag forcestart &8- &8&o(&7&o&7Force start a Gulag Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/gulag join &8- &8&o(&7&o&7Join ongoing Gulag Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/gulag leave &8- &8&o(&7&o&7Leave ongoing Gulag Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/gulag tp &8- &8&o(&7&o&7Teleport to the Gulag Event Arena&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/gulag setspawn  &8- &8&o(&7&o&7Set the spawns for Gulag Event&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
