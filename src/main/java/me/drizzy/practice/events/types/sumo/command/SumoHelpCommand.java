package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo", "sumo help"}, permission = "array.dev")
public class SumoHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lSumo &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/sumo cancel &8(&7&o&7Cancel current sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo cooldown &8(&7&o&7Reset the sumo Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo host &8(&7&o&7Host a sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo setknockback &8<&7knockback&8> &8(&7&o&7Set Sumo Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo forcestart &8(&7&o&7Force start a sumo events&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo join &8(&7&o&7Join ongoing sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo leave &8(&7&o&7Leave ongoing sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo tp &8(&7&o&7Teleport to the sumo Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo setspawn &8<&7one|two|spec&8> &8(&7&o&7Set the spawn locations for sumo Event&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
