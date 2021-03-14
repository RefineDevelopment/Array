package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit remove", "kit delete"}, permission = "array.dev")
public class KitRemoveCommand {
    public void execute(final Player player, @CPL("name") final String name) {
        if (name == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Please provide a name."));
            return;
        }
        final Kit kit = Kit.getByName(name);
        if (kit != null) {
            kit.delete();
            Kit.getKits().forEach(Kit::save);
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Removed the kit &b" + kit.getName() + "&7."));
        }
    }
}
