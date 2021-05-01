package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.essentials.listener.GoldenHeads;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"array goldenhead"}, permission="array.dev")
public class ArrayGoldenHeadCommand {
    public void execute(Player p, @CPL("[normal|bridge]") String type) {
        if (type.equalsIgnoreCase("bridge")) {
            p.sendMessage(CC.translate("&8[&c&lArray&8] &7You received a &cAdam's Apple&7."));
            p.getInventory().addItem(GoldenHeads.getBridgeApple());
            return;
        } else if (type.equalsIgnoreCase("normal")) {
            p.sendMessage(CC.translate("&8[&c&lArray&8] &7You received a &cGolden head&7."));
            p.getInventory().addItem(GoldenHeads.goldenHeadItem());
            return;
        }
        p.sendMessage(CC.translate("&8[&c&lArray&8] &7Please pick specify &c'normal' &7or &c'bridge' &7type."));
    }
}
