package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;
import me.drizzy.practice.array.menu.MainMenu;

@CommandMeta(label={"array manage", "manage"}, permission="array.dev")
public class ArrayManageCommand {
    public void execute(Player player) {
        new MainMenu().openMenu(player);
    }
}
