package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.hologram.HologramUtils;
import org.bukkit.entity.Player;

@CommandMeta(label= {"practice hologram create"}, permission="practice.staff")
public class HologramCommand {
    public void execute(Player player, @CPL("kit") String ladder) {

        if (ladder.equalsIgnoreCase("Default")) {

            new HologramUtils().HologramDefault(player.getLocation());
            player.sendMessage("Successfully created Default Hologram!");
        } else {
            new HologramUtils().HologramLadder(ladder, player.getLocation());
            player.sendMessage("Successfully created " + ladder + "Hologram!");
        }
    }
}
