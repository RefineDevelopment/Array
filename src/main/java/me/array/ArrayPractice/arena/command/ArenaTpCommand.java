package me.array.ArrayPractice.arena.command;

import org.bukkit.entity.*;
import me.array.ArrayPractice.arena.*;
import com.qrakn.honcho.command.*;

@CommandMeta(label = { "arena tp" }, permission = "practice.admin.arena")
public class ArenaTpCommand
{
    public void execute(final Player player, @CPL("Arena") final Arena arena) {
        if (arena != null) {
            player.teleport(arena.getSpawn1());
        }
    }
}
