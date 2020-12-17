package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.*;
import org.bukkit.command.*;
import me.array.ArrayPractice.util.external.*;
import me.array.ArrayPractice.arena.*;
import java.util.*;

@CommandMeta(label = { "arenas" }, permission = "practice.admin.arena")
public class ArenasCommand
{
    public void execute(final CommandSender sender) {
        sender.sendMessage(CC.AQUA + "Arenas:");
        if (Arena.getArenas().isEmpty()) {
            sender.sendMessage(CC.WHITE + "There are no arenas.");
            return;
        }
        for (final Arena arena : Arena.getArenas()) {
            if (arena.getType() != ArenaType.DUPLICATE) {
                sender.sendMessage(CC.GRAY + " - " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.GRAY + " (" + arena.getType().name() + ")");
            }
        }
    }
}
