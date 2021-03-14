package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef", "spleef help"}, permission = "array.dev")
public class SpleefHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&b&lSpleef &8- &8&o(&7&o&7Spleef Commands&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &b/slpeef cancel &8- &8&o(&7&o&7Cancel current Spleef Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/slpeef cooldown &8- &8&o(&7&o&7Reset the Spleef Event cooldown&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/slpeef host &8- &8&o(&7&o&7Host a Spleef Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/slpeef forcestart &8- &8&o(&7&o&7Forcestart a Spleef Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/slpeef join &8- &8&o(&7&o&7Join ongoing Spleef Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/slpeef leave &8- &8&o(&7&o&7Leave ongoing Spleef Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/slpeef tp &8- &8&o(&7&o&7Teleport to the Spleef Event Arena&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/slpeef setspawn  &8- &8&o(&7&o&7Set the spawns for Spleef Event&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
