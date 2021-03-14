package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.array.listener.GoldenHeads;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"array goldenhead"}, permission="array.dev")
public class ArrayGoldenHeadCommand {
    public void execute(Player p, @CPL("[normal|bridge]") String type) {
        if (type.equalsIgnoreCase("bridge")) {
            p.sendMessage(CC.translate("&8[&b&lArray&8] &7You received a &bAdam's Apple&7."));
            p.getInventory().addItem(GoldenHeads.getBridgeApple());
            return;
        } else if (type.equalsIgnoreCase("normal")) {
            p.sendMessage(CC.translate("&8[&b&lArray&8] &7You received a &bGolden head&7."));
            p.getInventory().addItem(GoldenHeads.goldenHeadItem());
            return;
        }
        p.sendMessage(CC.translate("&8[&b&lArray&8] &7Please pick specify &b'normal' &7or &b'bridge' &7type."));
    }
}
