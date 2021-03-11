package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena addkit", permission = "array.dev")
public class ArenaAddKitCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("kit") Kit kit) {
        if (arena == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Arena does not exist"));
            return;
        }

        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Kit does not exist"));
            return;
        }

        if (arena.getType() == ArenaType.SHARED && kit.getGameRules().isBuild()) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7The arena is set to type shared and you can't add build kits to it!"));
            return;
        }

        if (!arena.getKits().contains(kit.getName()))
            arena.getKits().add(kit.getName());

        player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully added the kit &b" + kit.getName() + "&7 to &b" + arena.getName()));
        arena.save();
    }

}