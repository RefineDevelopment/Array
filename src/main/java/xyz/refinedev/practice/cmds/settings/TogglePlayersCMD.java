package xyz.refinedev.practice.cmds.settings;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.settings.meta.SettingsMeta;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */

public class TogglePlayersCMD {

    @Command(name = "", desc = "Toggle Player Visibility for your Profile")
    public void toggle(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        SettingsMeta settings = profile.getSettings();

        settings.setShowPlayers(!settings.isShowPlayers());

        String enabled = Locale.SETTINGS_ENABLED.toString().replace("<setting_name>", "Player Visibility");
        String disabled = Locale.SETTINGS_DISABLED.toString().replace("<setting_name>", "Player Visibility");

        player.sendMessage(settings.isShowPlayers() ? enabled : disabled);
        profile.handleVisibility();
    }

}