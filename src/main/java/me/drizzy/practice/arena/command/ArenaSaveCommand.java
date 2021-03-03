package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "arena save", permission = "array.staff")
public class ArenaSaveCommand {

    public void execute(CommandSender sender) {
        for ( Arena arena : Arena.getArenas() ) {
            arena.save();
        }

        sender.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "Successfully saved all arenas!");
    }
}
