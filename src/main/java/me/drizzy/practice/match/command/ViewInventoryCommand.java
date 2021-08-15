package me.drizzy.practice.match.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.match.menu.MatchDetailsMenu;
import me.drizzy.practice.util.chat.CC;
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
