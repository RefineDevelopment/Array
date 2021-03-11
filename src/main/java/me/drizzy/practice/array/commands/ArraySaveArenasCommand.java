package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"array savearenas", "array arenas save"}, permission="array.dev")
public class ArraySaveArenasCommand {
    public void execute(Player p) {
        Arena.getArenas().forEach(Arena::save);
        p.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "Arenas have been saved!");
    }
}

