package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.hologram.HologramUtils;
import org.bukkit.entity.Player;

@CommandMeta(label= {"practice hologram create"}, permission="practice.staff")
public class HologramCommand {
    public void execute(Player player) {
        new HologramUtils(player.getLocation());
        player.sendMessage("Successfully created Hologram!");
    }
}
