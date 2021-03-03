package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="arena disablepearls", permission="array.dev")
public class ArenaDisablePearlsCommand {
    public void execute(Player player, @CPL("arena") Arena arena, @CPL("true/false") boolean enabled) {
        if (arena == null) {
            player.sendMessage(CC.translate("&cThat arena does not exist!"));
            return;
        }
        arena.setDisablePearls(enabled);
        player.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "Successfully " + (enabled ? "enabled" : "disabled") + " pearls in the arena " + arena.getName());
    }
}
