package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = { "arenas", "arena list" }, permission = "array.staff")
public class ArenasCommand {

    public void execute(CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» All Arenas"));
        player.sendMessage(CC.CHAT_BAR);

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

            StandaloneArena standaloneArena = (StandaloneArena) arena;
            
            if (arena.getType().equals(ArenaType.STANDALONE)) {
                player.sendMessage(CC.DARK_GRAY + " • " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.GRAY + " (" + standaloneArena.getDuplicates().size() + ") " + CC.translate((arena.isActive() ? " &8[&eIn-Match&8]" : " &8[&aFree&8]") + " &8[&7" + type + "&8]"));
            } else {
                player.sendMessage(CC.DARK_GRAY + " • " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.translate((arena.isActive() ? " &8[&eIn-Match&8]" : " &8[&aFree&8]") + " &8[&7" + type + "&8]"));
            }
        }
        player.sendMessage(CC.CHAT_BAR);
    }
}
