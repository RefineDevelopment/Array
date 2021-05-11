package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.arena.runnables.ArenaPasteRunnable;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.other.TaskUtil;
import org.bukkit.entity.Player;

@CommandMeta(label="arena generate", permission="array.dev")
public class ArenaGenerateCommand {
    public void execute(Player player, @CPL("Arena") Arena arena, @CPL("amount") Integer amount) {

        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name does not exist."));
            return;
        }

        if (arena.getType() != ArenaType.STANDALONE) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That arena is not standalone!"));
            return;
        }

        if (amount > 50) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That amount is too high!"));
            return;
        }

        StandaloneArena standaloneArena = (StandaloneArena) arena;

        TaskUtil.run(new ArenaPasteRunnable(standaloneArena, amount));

        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Pasting, Checking console for results...."));
        Arena.getArenas().forEach(Arena::save);

    }
}
