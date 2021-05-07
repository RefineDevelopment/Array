package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Drizzy
 * Created at 4/11/2021
 */
@CommandMeta(label = "arena setduplicatespawn", permission = "array.dev")
public class ArenaSetDuplicateCommand {

    public void execute(Player player, @CPL("arena")Arena arena, @CPL("[1|2]") Integer pos, @CPL("Duplicate Arena Number") Integer number) {
        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name does not exist."));
            return;
        }
        final Arena darena = ((StandaloneArena)arena).getDuplicates().get(number - 2);
        if (darena != null) {
            final Location loc2 = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
            if (pos.equals(1)) {
                darena.setSpawn1(loc2);
            }
            else if (pos.equals(2)) {
                darena.setSpawn2(loc2);
            }
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated the position of &c" + arena.getName() + "&8&o (&7&oPosition: " + pos + "&8&o) (&7&oDupe Arena #" + (number - 1) + "&8&o)"));
            arena.save();
        }
    }
}
