

package me.array.ArrayPractice.profile.stats.command;

import me.array.ArrayPractice.profile.stats.menu.StatsMenu;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "stats" })
public class StatsCommand
{
    public void execute(final Player player) {
        new StatsMenu().openMenu(player);
    }
}
