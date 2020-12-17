

package me.array.ArrayPractice.profile.command.staff;

import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Entity;
import com.qrakn.honcho.command.CPL;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "s", "tphere" }, permission = "practice.s")
public class SCommand
{
    public void execute(final Player player, @CPL("target") final Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "Target is not valid");
        }
        target.teleport((Entity)player);
    }
}
