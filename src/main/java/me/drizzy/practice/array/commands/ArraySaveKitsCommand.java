package me.drizzy.practice.array.commands;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"array savekits", "array kits save"}, permission="array.dev")
public class ArraySaveKitsCommand {
    public void execute(Player p) {
        Kit.getKits().forEach(Kit::save);
        p.sendMessage(CC.translate("&8[&b&lArray&8] &bSaving Kits..."));
    }
}
