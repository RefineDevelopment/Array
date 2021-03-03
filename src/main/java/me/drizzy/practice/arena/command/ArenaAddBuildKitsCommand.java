package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label="arena addbuildkits", permission="array.dev")
public class ArenaAddBuildKitsCommand {
    public void execute(Player player, @CPL("Arena") Arena arena) {

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena does not exist");
            return;
        }

        for ( Kit kit : Kit.getKits() ) {
            if (kit == null) {
                player.sendMessage(ChatColor.RED + "Kits don't not exist");
                return;
            }
            if (kit.getGameRules().isBuild()) {
                if (!arena.getKits().contains(kit.getName())) {
                    arena.getKits().add(kit.getName());
                }
            }
            player.sendMessage(CC.translate("&8[&b&lArray&8] &a") +  "Successfully added the kit " + kit.getName() + " to " + arena.getName());
        }
        arena.save();

    }
}