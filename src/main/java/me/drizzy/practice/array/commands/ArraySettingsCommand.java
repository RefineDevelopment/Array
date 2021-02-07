package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.settings.SettingsMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"options", "settings", "preferences"})
public class ArraySettingsCommand {

    public void execute(Player player) {
        new SettingsMenu().openMenu(player);
    }

}