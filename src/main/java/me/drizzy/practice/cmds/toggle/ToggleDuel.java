package me.drizzy.practice.cmds.toggle;

import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.settings.meta.SettingsMeta;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */

public class ToggleDuel {

    @Command(name = "", desc = "Toggle Duels for your Profile")
    public void toggleDuels(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        SettingsMeta settings = profile.getSettings();

        settings.setReceiveDuelRequests(!settings.isReceiveDuelRequests());

        String enabled = Locale.SETTINGS_ENABLED.toString().replace("<setting_name>", "Duel Requests");
        String disabled = Locale.SETTINGS_DISABLED.toString().replace("<setting_name>", "Duel Requests");

        player.sendMessage(settings.isReceiveDuelRequests() ? enabled : disabled);
    }

}
