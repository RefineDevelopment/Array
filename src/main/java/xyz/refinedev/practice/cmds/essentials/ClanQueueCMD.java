package xyz.refinedev.practice.cmds.essentials;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.queue.menu.QueueSelectKitMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Menu;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/4/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ClanQueueCMD {

    private final Array plugin;

    @Command(name = "", desc = "Open Clan Queue Menu")
    public void queue(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        if (!profile.hasClan()) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }
        if (!profile.isBusy()) {
            Menu menu = new QueueSelectKitMenu(QueueType.CLAN);
            plugin.getMenuHandler().openMenu(menu, player);
        } else {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
        }
    }
}
