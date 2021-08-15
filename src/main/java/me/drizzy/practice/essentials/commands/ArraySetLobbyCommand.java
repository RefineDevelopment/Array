package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.essentials.Essentials;
import org.bukkit.entity.Player;

@CommandMeta(label={"array setlobby", "array setspawn"}, permission="array.dev")
public class ArraySetLobbyCommand {
    public void execute(Player player) {
        Array.getInstance().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7You have set the &cnew &7lobby &cspawn &7!"));
    }
}
