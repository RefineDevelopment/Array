package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "arena setspawn" }, permission = "practice.staff")
public class ArenaSetSpawnCommand {

    public void execute(final Player player, @CPL("arena") final Arena arena, @CPL("A/B") String pos) {
        if (arena != null) {
            if (pos.equalsIgnoreCase("a")) {
                arena.setSpawnA(player.getLocation());
            } else if (pos.equalsIgnoreCase("b")) {
                arena.setSpawnB(player.getLocation());
            } else {
                player.sendMessage(CC.RED + "Invalid spawn point. Try \"a\" or \"b\".");
                return;
            }

            arena.save();

            player.sendMessage(CC.GREEN + "Updated spawn point \"" + pos + "\" for arena \"" + arena.getName() + "\"");
        } else {
            player.sendMessage(CC.RED + "An arena with that name doesn't exist.");
        }
    }

}
