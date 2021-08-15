package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setdisplayname", permission = "array.dev")
public class ArenaSetDisplayNameCommand {
     public void execute(Player player, @CPL("arena") String arenaname, @CPL("displayname") String displayname) {
         Arena arena = Arena.getByName(arenaname);
         if (arena == null) {
             player.sendMessage(CC.translate("&8[&c&lArray&8] &7Arena does not exist"));
             return;
         }
         arena.setDisplayName(displayname);
         arena.save();
         player.sendMessage(CC.translate("&8[&cArray&8] &7Successfully updated the arena &c" + arena.getName() + "'s &7display name."));


     }
}
