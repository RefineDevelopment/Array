package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label="arena addnormalkits", permission="practice.dev")
public class ArenaAddNormalKitCommand {
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
        if (kit.getGameRules().isBuild() || kit.getGameRules().isBoxuhc() || kit.getGameRules().isSpleef() || kit.getGameRules().isSumo() || kit.getGameRules().isParkour() || kit.getGameRules().isNoitems() || kit.getGameRules().isWaterkill()) {
            return;
        }

        if (!arena.getKits().contains(kit.getName()))
            arena.getKits().add(kit.getName());
        player.sendMessage(ChatColor.GREEN + "Successfully added the kit " + kit.getName() + " to " + arena.getName());
    }
        arena.save();

    }
}
