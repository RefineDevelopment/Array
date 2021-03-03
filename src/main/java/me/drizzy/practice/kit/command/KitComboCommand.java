package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit combo"}, permission = "array.dev")
public class KitComboCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isCombo()) {
                kit.getGameRules().setCombo(false);
            } else if (!kit.getGameRules().isCombo()) {
                kit.getGameRules().setCombo(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "Kit set combo mode to " + (kit.getGameRules().isCombo() ? "true!" : "false!"));
            player.sendMessage(CC.translate("&8[&bTIP&8] &7This will set the No-Damage Ticks to 2 and players will be able to hit faster!"));
        }
    }
}
