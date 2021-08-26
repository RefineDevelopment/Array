package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.menu.MatchDetailsMenu;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

import java.util.UUID;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class ViewInvCommand {

    @Command(name = "", desc = "Open match details menu", usage = "<uuid>")
    public void viewInventory(@Sender Player player, String id) {
        MatchSnapshot cachedInventory;

        try {
            cachedInventory = MatchSnapshot.getByUuid(UUID.fromString(id));
        } catch (Exception e) {
            cachedInventory = MatchSnapshot.getByName(id);
        }

        if (cachedInventory == null) {
            player.sendMessage(CC.RED + "ID provided for the inventory has been expired.");
            return;
        }

        new MatchDetailsMenu(cachedInventory, null).openMenu(player);
    }
}
