package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit editable", "kit enable editable"}, permission = "array.dev")
public class KitEditableCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
} else {
            if (kit.getGameRules().isEditable()) {
                kit.getGameRules().setEditable(false);
            } else if (!kit.getGameRules().isEditable()) {
                kit.getGameRules().setEditable(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "Kit set editable mode to " + (kit.getGameRules().isBuild() ? "true!" : "false!"));
        }
    }
}
