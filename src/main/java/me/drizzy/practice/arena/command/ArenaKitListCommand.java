package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"arena listkits", "arena kitlist", "arena kits"}, permission="array.dev")
public class ArenaKitListCommand {
    public void execute(Player player, @CPL("arena")Arena arena) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate( "&bArray &7» " + arena.getName() + "'s kits"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        for ( String string : arena.getKits() ) {
            Kit kit = Kit.getByName(string);
        if (kit == null) {
            player.sendMessage("");
            player.sendMessage(CC.GRAY + CC.ITALIC + "There are no kits for this arena.");
            player.sendMessage("");
            return;
        }
        player.sendMessage(CC.GRAY + " • " + kit.getName());
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
    }
}
