package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.array.listener.GoldenHeads;
import me.drizzy.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"array goldenhead"}, permission="array.dev")
public class ArrayGoldenHeadCommand {
    public void execute(Player p) {
        p.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "Gave a golden head!");
        p.getInventory().addItem(GoldenHeads.goldenHeadItem());
    }
}
