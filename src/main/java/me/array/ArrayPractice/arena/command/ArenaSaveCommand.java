package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "arena save", permission = "practice.staff")
public class ArenaSaveCommand {

    public void execute(CommandSender sender) {
        for ( Arena arena : Arena.getArenas() ) {
            arena.save();
        }

        sender.sendMessage(ChatColor.GREEN + "Saved all arenas!");
    }
}
