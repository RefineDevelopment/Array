package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"lms", "lms help"}, permission = "array.dev")
public class LMSHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lLMS &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/lms cancel &8(&7&o&7Cancel current LMSEvent&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms cooldown &8(&7&o&7Reset the LMSEvent cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms host &8(&7&o&7Host a LMSEvent&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms setknockback &8<&7knockback&8> &8(&7&o&7Set LMS Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms forcestart &8(&7&o&7Forcestart a LMSEvent&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms join &8(&7&o&7Join ongoing LMSEvent&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms leave &8(&7&o&7Leave ongoing LMSEvent&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms tp &8(&7&o&7Teleport to the LMSEvent Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms setspawn &8(&7&o&7Set the spawn location for LMSEvent&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}