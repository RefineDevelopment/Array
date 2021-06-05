package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.match.menu.MatchDetailsMenu;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This Project is the property of Purge Community © 2021
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
