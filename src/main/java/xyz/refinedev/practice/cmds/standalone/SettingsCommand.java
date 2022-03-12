package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.settings.menu.ProfileSettingsMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Menu;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/31/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SettingsCommand {

    private final Array plugin;

    @Command(name = "", desc = "Open Array Settings Menu")
    public void settings(@Sender Player player) {
        FoldersConfigurationFile config = plugin.getMenuHandler().getConfigByName("profile_settings");
        Menu menu = new ProfileSettingsMenu(config);
        plugin.getMenuHandler().openMenu(menu, player);
    }
}
