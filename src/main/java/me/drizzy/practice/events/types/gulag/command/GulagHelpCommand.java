package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"gulag", "gulag help"}, permission = "array.dev")
public class GulagHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lGulag &8(&7&o&7Commands List&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/gulag cancel &8(&7&o&7Cancel current Gulag Event&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag cooldown &8(&7&o&7Reset the Gulag Event cooldown&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag host &8(&7&o&7Host a Gulag Event&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag setknockback &8<&7knockback&8> &8(&7&o&7Set Gulag Knockback Profile&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag forcestart &8(&7&o&7Force start a Gulag Event&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag join &8(&7&o&7Join ongoing Gulag Event&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag leave &8(&7&o&7Leave ongoing Gulag Event&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag tp &8(&7&o&7Teleport to the Gulag Event Arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/gulag setspawn &8<&7one|two|spec&8> &8(&7&o&7Set the spawn locations for Gulag Event&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
