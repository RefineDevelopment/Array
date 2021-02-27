package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setspawn", permission = "practice.dev")
public class ArenaSetSpawnCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("1/2") Integer pos) {
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena does not exist");
            return;
        }

        Location loc=new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

        if (pos.equals(1)) {
            arena.setSpawn1(loc);
        } else if (pos.equals(2)) {
            arena.setSpawn2(loc);
        }
        player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully set the position of &b" + arena.getName() + "&7 (&bPosition: &f" + pos + "&7)"));
        arena.save();

    }

}