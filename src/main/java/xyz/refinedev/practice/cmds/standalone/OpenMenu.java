package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.command.annotation.Text;
import xyz.refinedev.practice.util.menu.Menu;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/11/2021
 * Project: Array
 */

public class OpenMenu {

    @Command(name = "", desc = "Open a menu by name")
    public void openMenu(@Sender Player player, @Text String name) {
        Menu menu = Array.getInstance().getMenuManager().findMenu(name);
        if (menu == null) {
            player.sendMessage(CC.translate("&7Invalid Menu!"));
            return;
        }
        menu.openMenu(player);
    }
}
