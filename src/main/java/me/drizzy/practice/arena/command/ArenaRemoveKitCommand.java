package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label ={"arena removekit", "arena deletekit"}, permission = "array.dev")
public class ArenaRemoveKitCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("kit") Kit kit) {
        if (arena == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7An arena with that name does not exist"));
            return;
        }

        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7A kit with that name does not exist."));
            return;
        }

        if (arena.getKits().contains(kit.getName())) {
            arena.getKits().remove(kit.getName());

            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully removed the kit &b" + kit.getName() + " &7from &b" + arena.getName()));
            arena.save();
        }
    }

}