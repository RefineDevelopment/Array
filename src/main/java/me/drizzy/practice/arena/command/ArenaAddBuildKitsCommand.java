package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="arena addbuildkits", permission="array.dev")
public class ArenaAddBuildKitsCommand {
    public void execute(Player player, @CPL("Arena") Arena arena) {

        if (arena == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7An arena with that name does not exist."));
            return;
        }

        for ( Kit kit : Kit.getKits() ) {
            if (kit == null) {
                player.sendMessage(CC.translate("&8[&b&lArray&8] &7There are no kits setup."));
                return;
            }
            if (kit.getGameRules().isBuild()) {
                if (!arena.getKits().contains(kit.getName())) {
                    arena.getKits().add(kit.getName());
                }
            }
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully added the kit &b" + kit.getName() + "&7 to &b" + arena.getName()));
        }
        arena.save();

    }
}
