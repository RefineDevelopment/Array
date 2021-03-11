package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "arena save", permission = "array.dev")
public class ArenaSaveCommand {

    public void execute(CommandSender sender) {
        for ( Arena arena : Arena.getArenas() ) {
            arena.save();
        }
        sender.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully saved &b" + Arena.getArenas().size() + " &7arenas!"));
    }
}
