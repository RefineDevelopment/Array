package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit remove", permission = "practice.dev")
public class KitRemoveCommand {
    public void execute(final Player player, @CPL("name") final String name) {
        if (name == null) {
            player.sendMessage("Enter a name");
            return;
        }
        final Kit kit = Kit.getByName(name);
        if (kit != null) {
            kit.delete();
            Kit.getKits().forEach(Kit::save);
            Queue.getQueues().clear();
            Kit.getKits().clear();
            Kit.preload();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c")) + "Kit " + name + " removed");
        }
    }
}
