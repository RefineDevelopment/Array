package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.options.OptionsMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"options", "settings", "preferences"})
public class OptionsCommand {

    public void execute(Player player) {
        new OptionsMenu().openMenu(player);
    }

}