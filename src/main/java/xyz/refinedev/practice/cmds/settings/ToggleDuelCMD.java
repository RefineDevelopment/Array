package xyz.refinedev.practice.cmds.settings;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.settings.ProfileSettings;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/7/2021
 * Project: Array
 */
@RequiredArgsConstructor
public class ToggleDuelCMD {

    private final Array plugin;

    @Command(name = "", desc = "Toggle Duels for your Profile")
    public void toggleDuels(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        ProfileSettings settings = profile.getSettings();

        settings.setReceiveDuelRequests(!settings.isReceiveDuelRequests());

        String enabled = Locale.SETTINGS_ENABLED.toString().replace("<setting_name>", "Duel Requests");
        String disabled = Locale.SETTINGS_DISABLED.toString().replace("<setting_name>", "Duel Requests");

        player.sendMessage(settings.isReceiveDuelRequests() ? enabled : disabled);
    }
}
