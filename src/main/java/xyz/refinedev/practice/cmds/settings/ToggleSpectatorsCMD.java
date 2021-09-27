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

public class ToggleSpectatorsCMD {

    @Command(name = "", desc = "Toggle Spectators for your Profile")
    public void toggle(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        SettingsMeta settings = profile.getSettings();

        settings.setAllowSpectators(!settings.isAllowSpectators());

        String enabled = Locale.SETTINGS_ENABLED.toString().replace("<setting_name>", "Spectators");
        String disabled = Locale.SETTINGS_DISABLED.toString().replace("<setting_name>", "Spectators");

        player.sendMessage(settings.isAllowSpectators() ? enabled : disabled);
    }

}
