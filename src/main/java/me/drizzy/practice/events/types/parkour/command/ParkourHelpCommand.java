package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour", "parkour help"}, permission = "array.dev")
public class ParkourHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lParkour &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/parkour cancel &8(&7&o&7Cancel current Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour cooldown &8(&7&o&7Reset the Parkour Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour host &8(&7&o&7Host a Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour setknockback &8<&7knockback&8> &8(&7&o&7Set Parkour Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour forcestart &8(&7&o&7Force Start a Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour join &8(&7&o&7Join ongoing Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour leave &8(&7&o&7Leave ongoing Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour tp &8(&7&o&7Teleport to the Parkour Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour setspawn &8(&7&o&7Set the spawn locations for Parkour Event&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
