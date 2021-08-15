package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.selection.Selection;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"arena wand", "arena portalwand", "arena selection", "arena portal"}, permission = "array.dev")
public class ArenaSelectionWandCommand {
    public void execute(Player player) {
        if (player.getInventory().first(Selection.SELECTION_WAND) != -1) {
            player.getInventory().remove(Selection.SELECTION_WAND);
        } else {
            player.getInventory().addItem(Selection.SELECTION_WAND);
            player.sendMessage(CC.translate("&8[&cTIP&8] &7&oLeft-Click to select first position and Right-Click to select second position."));
            player.sendMessage(CC.translate("&7&oTo setup the cuboids, please select one position as the lower corner and one position as upper corner."));
        }

        player.updateInventory();
    }
}
