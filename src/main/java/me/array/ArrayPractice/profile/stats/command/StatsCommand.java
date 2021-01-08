package me.array.ArrayPractice.profile.stats.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.stats.menu.ELOMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"stats", "profile", "player", "elo"})
public class StatsCommand {

    public void execute(Player player) {
        new ELOMenu(player).openMenu(player);
    }

    public void execute(Player player, @CPL("player") Player target) {
        if (target == null)
            return;

        new ELOMenu(target).openMenu(player);
    }

}
