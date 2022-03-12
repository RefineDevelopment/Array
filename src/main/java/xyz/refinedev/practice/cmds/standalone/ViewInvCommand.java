package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.MatchManager;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.menu.MatchDetailsMenu;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.MenuHandler;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ViewInvCommand {

    private final Array plugin;

    @Command(name = "", desc = "Open match details menu", usage = "<uuid>")
    public void viewInventory(@Sender Player player, String id) {
        MatchManager matchManager = plugin.getMatchManager();
        MenuHandler menuHandler = plugin.getMenuHandler();
        MatchSnapshot cachedInventory = matchManager.getByString(id);

        if (cachedInventory == null) {
            player.sendMessage(CC.RED + "That inventory does not exist or has been expired.");
            return;
        }

        FoldersConfigurationFile config = menuHandler.getConfigByName("general");
        Menu menu = new MatchDetailsMenu(config, cachedInventory,null);
        menuHandler.openMenu(menu, player);
    }
}
