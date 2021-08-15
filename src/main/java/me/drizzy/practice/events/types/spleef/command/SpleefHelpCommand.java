package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef", "spleef help"}, permission = "array.dev")
public class SpleefHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lSpleef &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/spleef cancel &8(&7&o&7Cancel current Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef cooldown &8(&7&o&7Reset the Spleef Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef host &8(&7&o&7Host a Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef setknockback &8<&7knockback&8> &8(&7&o&7Set Spleef Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef forcestart &8(&7&o&7Forcestart a Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef join &8(&7&o&7Join ongoing Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef leave &8(&7&o&7Leave ongoing Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef tp &8(&7&o&7Teleport to the Spleef Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef setspawn &8(&7&o&7Set the spawn location for Spleef Event&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
