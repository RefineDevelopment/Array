package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo", "sumo help"}, permission = "array.dev")
public class SumoHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&b&lSumo &8- &8&o(&7&o&7Sumo Commands&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &b/sumo cancel &8- &8&o(&7&o&7Cancel current sumo Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/sumo cooldown &8- &8&o(&7&o&7Reset the sumo Event cooldown&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/sumo host &8- &8&o(&7&o&7Host a sumo Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/sumo forcestart &8- &8&o(&7&o&7Force start a sumo event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/sumo join &8- &8&o(&7&o&7Join ongoing sumo Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/sumo leave &8- &8&o(&7&o&7Leave ongoing sumo Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/sumo tp &8- &8&o(&7&o&7Teleport to the sumo Event Arena&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/sumo setspawn  &8- &8&o(&7&o&7Set the spawns for sumo Event&8&o)"));
        player.sendMessage(Color.translate("&7(one = First spawn, two = Second spawn, spec = Spectator spawn&7)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
