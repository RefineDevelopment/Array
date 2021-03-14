package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.chat.Color;
import org.bukkit.entity.Player;

@CommandMeta(label = {"ffa", "ffa help", "lms", "lms help"}, permission = "array.dev")
public class LMSHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&b&lLMS/FFA &8- &8&o(&7&o&7LMS/FFA Commands&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &b/lms cancel &8- &8&o(&7&o&7Cancel current LMS/FFA Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/lms cooldown &8- &8&o(&7&o&7Reset the LMS/FFA Event cooldown&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/lms host &8- &8&o(&7&o&7Host a LMS/FFA Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/lms forcestart &8- &8&o(&7&o&7Forcestart a LMS/FFA Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/lms join &8- &8&o(&7&o&7Join ongoing LMS/FFA Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/lms leave &8- &8&o(&7&o&7Leave ongoing LMS/FFA Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/lms tp &8- &8&o(&7&o&7Teleport to the LMS/FFA Event Arena&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/lms setspawn  &8- &8&o(&7&o&7Set the spawns for LMS/FFA Event&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}