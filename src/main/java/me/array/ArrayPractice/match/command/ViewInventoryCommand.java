package me.array.ArrayPractice.match.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.match.MatchSnapshot;
import me.array.ArrayPractice.match.menu.MatchDetailsMenu;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandMeta(label = "viewinv")
public class ViewInventoryCommand {

    public void execute(Player player, String id) {
        MatchSnapshot cachedInventory;

        try {
            cachedInventory = MatchSnapshot.getByUuid(UUID.fromString(id));
        } catch (Exception e) {
            cachedInventory = MatchSnapshot.getByName(id);
        }

        if (cachedInventory == null) {
            player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
            return;
        }

        new MatchDetailsMenu(cachedInventory, null).openMenu(player);
    }

}
