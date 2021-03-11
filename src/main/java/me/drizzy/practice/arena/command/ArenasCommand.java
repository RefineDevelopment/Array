package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = { "arenas", "arena list" }, permission = "array.staff")
public class ArenasCommand
{
    public void execute(CommandSender player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate( "&bArray &7» All Arenas"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        if (Arena.getArenas().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(CC.GRAY + CC.ITALIC + "There are no arenas setup.");
            player.sendMessage("");
            return;
        }
        for (final Arena arena : Arena.getArenas()) {
            String type;
            switch (arena.getType()) {
                case STANDALONE:
                    type = "Standalone";
                    break;
                case THEBRIDGE:
                    type = "TheBridge";
                    break;
                default:
                    type = "Shared";
            }
                player.sendMessage(CC.DARK_GRAY + " • " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.translate((arena.isActive() ? " &8[&eIn-Match&8]" : " &8[&aFree&8]") + " &8[&7" + type + "&8]"));
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
    }
}
