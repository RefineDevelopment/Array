package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit combo"}, permission = "array.dev")
public class KitComboCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isCombo()) {
                kit.getGameRules().setCombo(false);
            } else if (!kit.getGameRules().isCombo()) {
                kit.getGameRules().setCombo(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated combo mode for &b" + kit.getName() + " &7to &b" + (kit.getGameRules().isCombo() ? "true!" : "false!")));
            player.sendMessage(CC.translate("&8[&bTIP&8] &7This will set the No-Damage Ticks to 2 and players will be able to hit faster!"));
        }
    }
}
