package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets", "brackets help"}, permission = "array.dev")
public class BracketsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lBrackets &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/brackets cancel &8(&7&o&7Cancel current Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets cooldown &8(&7&o&7Reset the Brackets Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets host &8(&7&o&7Host a Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets setknockback &8<&7knockback&8> &8(&7&o&7Set Brackets Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets forcestart &8(&7&o&7Force start a Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets join &8(&7&o&7Join ongoing Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets leave &8(&7&o&7Leave ongoing Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets tp &8(&7&o&7Teleport to the Brackets Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets setspawn &8<&7one|two|spec&8> &8(&7&o&7Set the spawn locations for Brackets&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
