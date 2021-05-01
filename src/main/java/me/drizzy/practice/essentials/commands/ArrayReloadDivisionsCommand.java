package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"reloaddivisions", "reload divions", "array reload divisions", "array reloaddivisions"}, permission = "array.dev")
public class ArrayReloadDivisionsCommand {
    public void execute(Player player) {
        Array.getInstance().getDivisionsManager().loadDivisions();
        player.sendMessage(CC.translate("&7Successfully &creloaded &7divisions!"));
    }
}
