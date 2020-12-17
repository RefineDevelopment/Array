package me.array.ArrayPractice.profile.command;

import me.array.ArrayPractice.profile.options.OptionsMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "options", "settingsmain", "practicesettings", "toggle", "settings" })
public class OptionsCommand
{
    public void execute(final Player player) {
        new OptionsMenu().openMenu(player);
        player.sendMessage(ChatColor.GRAY + "Now viewing settings menu.");
    }

}
