package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena addkit", permission = "practice.dev")
public class ArenaAddKitCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("kit") Kit kit) {
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena does not exist");
            return;
        }

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
            return;
        }

        if (!arena.getKits().contains(kit.getName()))
            arena.getKits().add(kit.getName());

        player.sendMessage(ChatColor.GREEN + "Successfully added the kit " + kit.getName() + " to " + arena.getName());
        arena.save();
    }

}