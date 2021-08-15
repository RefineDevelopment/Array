package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="arena addnormalkits", permission="array.dev")
public class ArenaAddNormalKitCommand {
    public void execute(Player player, @CPL("Arena") Arena arena) {

        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Arena does not exist."));
            return;
        }

        for ( Kit kit : Kit.getKits() ) {
            if (kit == null) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7There are no kits."));
                return;
            }
        if (kit.getGameRules().isBuild() || kit.getGameRules().isBoxUHC() || kit.getGameRules().isSpleef() || kit.getGameRules().isSumo() || kit.getGameRules().isParkour() || kit.getGameRules().isWaterKill()) {
            return;
        }

        if (!arena.getKits().contains(kit.getName())) arena.getKits().add(kit.getName());
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully added the kit &c" + kit.getName() + "&7 to &c" + arena.getName() + "&7."));
    }
        arena.save();

    }
}
