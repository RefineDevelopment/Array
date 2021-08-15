package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="arena disablepearls", permission="array.dev")
public class ArenaDisablePearlsCommand {
    public void execute(Player player, @CPL("arena") Arena arena) {
        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That arena does not exist."));
            return;
        }
        if (arena.isDisablePearls()) {
            arena.setDisablePearls(false);
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully &cenabled &7pearls in the arena &c" + arena.getName()));
        } else {
            arena.setDisablePearls(true);
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully &cdisabled &7pearls in the arena &c" + arena.getName()));
        }
    }
}
