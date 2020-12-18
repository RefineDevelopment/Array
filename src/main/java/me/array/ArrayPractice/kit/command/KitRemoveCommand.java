package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit remove", permission = "practice.staff")
public class KitRemoveCommand {
    public void execute(final Player player, @CPL("name") final String name) {
        if (name == null) {
            player.sendMessage("Enter a name");
            return;
        }
        final Kit kit = Kit.getByName(name);
        if (kit != null) {
            kit.delete();
            Kit.getKits().remove(kit);
            player.sendMessage(ChatColor.RED + "Kit " + name + " removed");
        }
    }
}
