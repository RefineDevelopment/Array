package me.drizzy.practice.events.types.oitc.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"ffa", "ffa help", "OITC", "OITC help"}, permission = "array.dev")
public class OITCHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&c&lOITC/FFA &8(&7&o&7OITC/FFA Commands&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &c/OITC cancel &8(&7&o&7Cancel current OITC/FFA Event&8)"));
        player.sendMessage(Color.translate(" &8• &c/OITC cooldown &8(&7&o&7Reset the OITC/FFA Event cooldown&8)"));
        player.sendMessage(Color.translate(" &8• &c/OITC host &8(&7&o&7Host a OITC/FFA Event&8)"));
        player.sendMessage(Color.translate(" &8• &c/OITC forcestart &8(&7&o&7Forcestart a OITC/FFA Event&8)"));
        player.sendMessage(Color.translate(" &8• &c/OITC join &8(&7&o&7Join ongoing OITC/FFA Event&8)"));
        player.sendMessage(Color.translate(" &8• &c/OITC leave &8(&7&o&7Leave ongoing OITC/FFA Event&8)"));
        player.sendMessage(Color.translate(" &8• &c/OITC tp &8(&7&o&7Teleport to the OITC/FFA Event Arena&8)"));
        player.sendMessage(Color.translate(" &8• &c/OITC setspawn  &8(&7&o&7Set the spawns for OITC/FFA Event&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}