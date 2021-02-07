package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"array setlobby", "array setspawn"}, permission="practice.dev")
public class ArraySetLobbyCommand {
    public void execute(Player player) {
        Array.getInstance().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "You have set the new lobby!");
    }
}
