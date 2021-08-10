package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.profile.menu.KillEffectsMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/10/2021
 * Project: Array
 */

public class KillEffectsCMD {

    @Command(name = "", desc = "Open Kill Effects Menu")
    public void killEffect(@Sender Player player) {
        new KillEffectsMenu().openMenu(player);
    }
}
